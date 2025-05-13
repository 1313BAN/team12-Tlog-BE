package com.ssafy.tlog.config.jwt;

import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    // HTTP 요청이 들어올 때마다 실행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // request에 access 헤더 찾기
        String access = request.getHeader("access");
        if(access != null && !access.startsWith("Bearer ")) {
            // 토큰이 없는 경우 다음 필터로 요청을 전달
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출 및 검증
        // access에 Bearer
        String accessToken = access.split(" ")[1];
        if(jwtUtil.isExpired(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 사용자 정보 추출 및 인증 처리
        int userId = jwtUtil.getUserId(accessToken);
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword("dumy");
        user.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // SecurityContext에 Authentication 설정 -> 요청 처리가 완료되면 사라짐
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
