package com.pear.shop.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm; // 0.12.x에서는 이거 대신 Jwts.SIG 사용 권장
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    static {
        try {
            // 파일에서 개인키 읽어오기
            String privateKeyContent = new String(Files.readAllBytes(Paths.get("private.key")));
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(privateKeySpec);


            // 파일에서 공개키 읽어오기
            String publicKeyContent = new String(Files.readAllBytes(Paths.get("public.key")));
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);

            System.out.println("RSA 키 파일을 로드했습니다!");

        } catch (Exception e) {
            throw new RuntimeException("키 파일을 읽을 수 없습니다. KeyMaker를 먼저 실행해주세요.", e);
        }
    }

    // 토큰 생성
    public static String createToken(String username, String displayName) {
        long expireTimeMs = 1000 * 60 * 60; // 1시간

        return Jwts.builder()
                .claim("username", username)
                .claim("displayName", displayName)
                .claim("role", "ROLE_USER")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    // Refresh Token 생성
    public static String createRefreshToken(String username){
        long expireTimeMs = 24 * 60 * 60 * 1000L;

                return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(privateKey, Jwts.SIG.RS256) // 파일에 있는 개인키 사용
                .compact();
    }

    // 토큰 검증 및 해독
    public static Claims extractToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 부가 메서드들
    public static String getUsername(String token) {
        return extractToken(token).get("username", String.class); // 타입 안전하게 가져오기
    }

    public static String getDisplayName(String token) {
        String dn = extractToken(token).get("displayName", String.class);
        return dn != null ? dn : "";
    }

    public static String getRole(String token) {
        String role = extractToken(token).get("role", String.class);
        return role != null ? role : "ROLE_USER";
    }

    // 만료 여부 확인
    public static boolean isExpired(String token) {
        try {
            return extractToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}


    // 메모리 영역에서의 RSA256 사용법
//    // 1. 키  쌍(KeyPair) 생성
//    // 서버가 켜질 때마다 생성되며, 실무로 사용할 경우 파일로 저장해서 불러와야 함
//    private static final KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
//
//    // 2. 토큰 생성 Sign (개인키로 수행)
//    public static String createToken(String username, String displayName){
//        // 유효기간 지정
//        long expireTimeMs = 1000 * 60 * 60;
//
//        return Jwts.builder()
//                .claim("username", username)
//                .claim("displayName", displayName)
//                .claim("role", "ROLE_USER")
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
//                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
//                .compact();
//    }
//
//    // 3. 토큰 검증 및 해독 ( 이 부분은 공개키로 수행)
//    public static Claims extractToken(String token){
//        return Jwts.parser()
//                .setSigningKey(keyPair.getPublic())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // 4. 만료 여부 확인 메서드
//    public static boolean isExpired(String token){
//        try{
//            // 날짜 확인 메서드
//            Date expiredDate = extractToken(token).getExpiration();
//            return expiredDate.before(new Date());
//        } catch(Exception e){
//            return true; // 에러가 나면 만료 처리
//        }
//    }
//
//    // 부가 메서드들
//    public static String getUsername(String token){
//        return extractToken(token).get("username").toString();
//    }
//
//    public static String getDisplayName(String token){
//        Object dn = extractToken(token).get("displayName");
//        return dn != null ? dn.toString() : "";
//    }
//
//    public static String getRole(String token){
//        Object role = extractToken(token).get("role");
//        return role != null ? role.toString() : "ROLE_USER";
//    }
//}