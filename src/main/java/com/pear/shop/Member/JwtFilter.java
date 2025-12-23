package com.pear.shop.Member;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if(request.getMethod().equals("OPTIONS")){
            filterChain.doFilter(request, response);
            return;
        }
        // token 지정
        String token = null;

        // 1. Authorization 에서 먼저 찾기 (localStorage 저장소 사용 방식)
        String authHeader = request.getHeader("Authorization");

//        System.out.println("요청 Method: " + request.getMethod());
//        System.out.println("요청 URL: " + request.getRequestURI());
//        System.out.println("헤더 값: " + authHeader);

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7); // 순수 토큰만 가져오기
            System.out.println("토큰 발견");
        }
        
        // 2. 토큰이 없다면 게스트로 통과시킴
        if(token == null){
//            System.out.println("게스트로 통과");
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 토큰 검증 및 인증 정보 생성
        try {
            // 토큰 해독 코드
            Claims claim = JwtUtil.extractToken(token);

            // 권한 정보 파싱
            String role = (String) claim.get("role");
            if(role == null){
                role = "ROLE_USER";
            }

            // 권한 리스트
            var authorities = java.util.List.of(new SimpleGrantedAuthority(role));

            // CustomUser 생성
            var customUser = new CustomUser(
                    claim.get("username").toString(),
                    "none", // 토큰에 없는 비밀번호는 임의값으로 지정
                    authorities
            );

            // 닉네임 가져와서 넣기
            if (claim.get("displayName") != null) {
                customUser.displayName = claim.get("displayName").toString();
            }

            // 인증 토큰 생성
            var authToken = new UsernamePasswordAuthenticationToken(
                    customUser, "", authorities
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


            // SecurityContextHolder 에 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("인증 성공: " + customUser.getUsername());

        } catch (Exception e){
            System.out.println("토큰 검증 실패" + e.getMessage());
    }

        filterChain.doFilter(request, response);



    // 쿠키 방식 Authorization
//        // 사용자가 쿠키 제출 시 수행 될 코드
//        Cookie[] cookies = request.getCookies();
//        if(cookies == null){
//            filterChain.doFilter(request, response);
//            return;
//        }

//        // jwt 라는 쿠키를 꺼내서 jwtCookie 에 저장
//        var jwtCookie = "";
//        for(int i = 0; i < cookies.length; i++){
//            if(cookies[i].getName().equals("jwt") ){
//                jwtCookie = cookies[i].getValue();
//            }
//        }
//        System.out.println(jwtCookie);


//        // jwtCookie에 저장된 값이 이상한 점이 없는지 확인
//        Claims claim;
//        try{
//            claim = JwtUtil.extractToken(jwtCookie);
//        } catch(Exception e){
//            filterChain.doFilter(request, response);
//            return;
//        }


//        // 그러면 사용자가 제출한 값이 저장되어 있는 jwtCookie 의 값이 auth 에 저장
//        var arr = claim.get("authorities").toString().split(",");
////        System.out.println(arr);
//        var authorities = Arrays.stream(arr)
//                .map(a -> new SimpleGrantedAuthority(a)).toList();
//
//        var customUser = new CustomUser(
//                claim.get("username").toString(),
//                "none",
//                authorities
//        );
//        customUser.displayName = claim.get("displayName").toString();
//
//        var authToken = new UsernamePasswordAuthenticationToken(
//                customUser,"", authorities
//        );
//        authToken.setDetails(new WebAuthenticationDetailsSource()
//                .buildDetails(request)
//        );
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        filterChain.doFilter(request, response);
    }
}
