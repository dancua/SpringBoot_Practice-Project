package com.pear.shop.Member;

import com.pear.shop.refresh.AuthDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    // 메모리 랜덤 키 생성 하는 코드

//    // RSA 키 쌍 변수
//    private PrivateKey privateKey;
//    private PublicKey publicKey;
//
//    // Refresh Token 저장소 (DB 대용)
//    private final Set<String> refreshTokenStore = new HashSet<>();
//
//    // 서버 시작 시 RSA 키 쌍 생성
//    @PostConstruct
//    public void initKeys() throws NoSuchAlgorithmException {
//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//        keyGen.initialize(2048);
//        KeyPair pair = keyGen.generateKeyPair();
//        this.privateKey = pair.getPrivate();
//        this.publicKey = pair.getPublic();
//        System.out.println("RSA Key Pair Generated!");
//    }

    @GetMapping("/register")
    String register(){
        return "/member";
    }


    @PostMapping("/setRegister")
    String setRegister(@RequestParam String username,
                       @RequestParam String password,
                       @RequestParam String displayName,
                       Model model){
        try {
            memberService.addMember(username, password, displayName);
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        return "redirect:/list/page/1";
    }

    @GetMapping("/login")
    public String login(){
        var result = memberRepository.findByUsername("test1");
        System.out.println(result.get().getDisplayName());
        return "login";
    }

    @GetMapping("/my-page")
    public String myPage(){
        return "mypage";
    }

    // 회원가입 API ( JSON 요청 처리)
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<String> signup(@RequestBody AuthDto.SignupRequest request){
        if(memberRepository.findByUsername(request.getUsername()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 아이디 입니다.");
        }

        // 회원가입에 대한 에러 처리
        try{
            memberService.addMember(request.getUsername(), request.getPassword(), request.getDisplayName());
            return ResponseEntity.ok("회원가입 완료");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가입 실패");
        }
    }

    // 로그인 (RSA 토큰 발급 및 Refresh Token)
    @PostMapping("/login/jwt")
    @ResponseBody
    public ResponseEntity<?> loginJWT(@RequestBody AuthDto.LoginRequest request){

        // DB에서 유저 찾기
        var member= memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 아이디입니다."));

        // 비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다");
        }

        // KeyMaker로 키를 생성하여 사용하는 방식
        String accessToken = JwtUtil.createToken(member.getUsername(), member.getDisplayName());
        String refreshToken = JwtUtil.createRefreshToken(member.getUsername());

        // Refresh Token DB에 저장
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        // JSON으로 반환
        return ResponseEntity.ok(new AuthDto.TokenResponse(accessToken, refreshToken));
    }

    // 토큰 갱신하는 코드
    @PostMapping("/refresh")
    @ResponseBody
    public ResponseEntity<AuthDto.TokenResponse> refresh(@RequestBody AuthDto.TokenRequest request){
        String refreshToken = request.getToken();

        // JwtUtil을 이용한 Refresh Token 유효성 검사
        if(refreshToken == null || JwtUtil.isExpired(refreshToken)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try{
            // 토큰에서 유저 정보 추출하기
            Claims claims = JwtUtil.extractToken(refreshToken);
            String username = claims.getSubject();

            // DB에 저장된 토큰과 일치하는지 확인
            var member = memberRepository.findByUsername(username);
            if(member.isEmpty() || !refreshToken.equals(member.get().getRefreshToken())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 새 Access Token 발급
            String newAccessToken = JwtUtil.createToken(username, member.get().getDisplayName());
            return ResponseEntity.ok(new AuthDto.TokenResponse(newAccessToken, refreshToken));
        } catch(JwtException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> logout(@RequestBody AuthDto.TokenRequest request){
        String token = request.getToken();
       if(token == null){
           return ResponseEntity.badRequest().build();
       }
       try{
           // 토큰에서 어떤 사용자가 로그아웃을 진행하는지 알아내는 코드
           Claims claims = JwtUtil.extractToken(token);
           String username = claims.getSubject();

           // DB에서 사용자 찾기
           var memberOptional = memberRepository.findByUsername(username);

           if(memberOptional.isPresent()){
               Member member = memberOptional.get();

               //  DB의 refresh Token을 null로 수정
               member.setRefreshToken(null);
               memberRepository.save(member);
           }
       } catch(Exception e){
            return ResponseEntity.status(204).body("로그아웃 됐습니다.");
       }
        return ResponseEntity.noContent().build();
    }

}
