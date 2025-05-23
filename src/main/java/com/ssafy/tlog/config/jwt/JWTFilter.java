package com.ssafy.tlog.config.jwt;

import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.InvalidUserException;
import com.ssafy.tlog.exception.custom.TokenExpiredException;
import com.ssafy.tlog.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    // HTTP 요청이 들어올 때마다 실행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 로그인 경로는 토큰 검증 건너뛰기
        if (Set.of(
                "/api/auth/login",
                "/api/auth/signup",
                "/api/auth/check-name"
        ).contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // request에 Authorization 헤더 찾기
        String access = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (access == null || !access.startsWith("Bearer ")) {
            // 토큰이 없는 경우 다음 필터로 요청을 전달
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출 및 검증, token에 Bearer
        String accessToken = access.split(" ")[1];
        if (jwtUtil.isExpired(accessToken)) {
            throw new TokenExpiredException("Access Token이 만료되었습니다.");
        }

        // 토큰에서 사용자 ID 추출
        int userId = jwtUtil.getUserId(accessToken);

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
                    customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            throw new InvalidUserException("유효하지 않은 사용자 정보입니다.");
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
