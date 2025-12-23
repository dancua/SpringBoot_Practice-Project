package com.pear.shop.refresh;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.security.*;
import java.util.*;

//@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // RSA 키 쌍 변수
    private PrivateKey privateKey;
    private PublicKey publicKey;

    // Refresh Token 저장소 (DB 대용)
    private final Set<String> refreshTokenStore = new HashSet<>();

    // 서버 시작 시 RSA 키 쌍 생성
    @PostConstruct
    public void initKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
        System.out.println("RSA Key Pair Generated!");
    }

    // ---------------------------------------------------------
    // 1. 회원가입
    // ---------------------------------------------------------
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthDto.SignupRequest request){

        // 1. 중복 아이디 있는지 확인
        if(userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 아이디 입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 유저 저장
        User newUser = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .nickname(request.getDisplayName())
                .role("ROLE_USER")
                .build();

        userRepository.save(newUser);

        return ResponseEntity.ok("회원가입 완료");
    }


    // ---------------------------------------------------------
    // 2. 로그인 (Login) - DB 연결
    // ---------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDto.LoginRequest request) {
        // 1. 아이디 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 아이디입니다."));

        // 2. 비밀번호 대조
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }

        // A. Access Token 생성 (테스트 유효기간 1시간)
        String accessToken = Jwts.builder()
                .subject(user.getUsername()) // 토큰의 주인
                .claim("role", user.getRole()) // 권한 정보
                .issuedAt(new Date()) // 날짜
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 유효 기간 1시간
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();

        // B. Refresh Token 생성 (유효기간 1일)
        String username = user.getUsername();
        String refreshToken = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();

        // Refresh Token 저장
        refreshTokenStore.add(refreshToken);

        return ResponseEntity.ok(new AuthDto.TokenResponse(accessToken, refreshToken));
    }

    // ---------------------------------------------------------
    // 3. 보호된 리소스 접근 (Access Token 검증)
    // ---------------------------------------------------------
    @GetMapping("/posts")
    public ResponseEntity<String> getPosts(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return ResponseEntity.ok("인증 성공! 환영합니다, " + claims.getSubject());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("유효하지 않거나 만료된 토큰입니다.");
        }
    }

    // ---------------------------------------------------------
    // 4. 토큰 갱신 (Refresh Token 사용)
    // ---------------------------------------------------------
    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.TokenResponse> refresh(@RequestBody AuthDto.TokenRequest request) {
        String refreshToken = request.getToken();

        if (!refreshTokenStore.contains(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            // Refresh Token 유효성 검증
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey) // verifyWith 사용
                    .build()
                    .parseSignedClaims(refreshToken) // parseSignedClaims 사용
                    .getPayload(); // getPayload 사용

            // 검증 통과 시 새 Access Token 발급
            String newAccessToken = Jwts.builder()
                    .subject(claims.getSubject())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 30 * 1000))
                    .signWith(privateKey, Jwts.SIG.RS256)
                    .compact();

            return ResponseEntity.ok(new AuthDto.TokenResponse(newAccessToken, refreshToken));

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // ---------------------------------------------------------
    // 5. 로그아웃
    // ---------------------------------------------------------
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody AuthDto.TokenRequest request) {
        refreshTokenStore.remove(request.getToken());
        return ResponseEntity.noContent().build();
    }

}