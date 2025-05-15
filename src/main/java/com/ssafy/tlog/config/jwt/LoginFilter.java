package com.ssafy.tlog.config.jwt;

import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.exception.custom.BadCredentialsException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Value("${access_expiration}")
    private long accessExpiration;

    @Value("${refresh_expiration}")
    private long refreshExpiration;

    // 로그인 시도 시 인증 로직 처리
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // username과 password를 검증하기 위한 토큰 -> principal, credentials, authorities
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, password, null);

        // 토큰 검증을 위해 AuthenticationManager로 전달
        return authenticationManager.authenticate(auth);
    }

    // 로그인 성공하면 실행 -> JWT 발급
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access = jwtUtil.createJwt("access",userId, username, role, accessExpiration);
        String refresh = jwtUtil.createJwt("refreh",userId, username, role, refreshExpiration);

        response.setHeader("access","Bearer "+access);
        response.addCookie(createCookie("refresh","Bearer"+refresh));
    }

    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(7*24*60*60);
        cookie.setPath("/api");
        cookie.setHttpOnly(true);
        return cookie;
    }

    // 로그인 실패하면 실행
    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        throw new BadCredentialsException("로그인에 실패하였습니다.");
    }
}
