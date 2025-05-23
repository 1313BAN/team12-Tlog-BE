package com.ssafy.tlog.config.security;

import com.ssafy.tlog.config.jwt.JWTFilter;
import com.ssafy.tlog.config.jwt.JWTUtil;
import com.ssafy.tlog.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public RoleHierarchy roleHierarchy() {
        // ROLE_ 접두사를 기본으로 사용하여 ADMIN 역할이 USER 역할을 포함하도록 설정
        return RoleHierarchyImpl.withDefaultRolePrefix().role("ADMIN").implies("USER").build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 인코딩 알고리즘을 지원하는 DelegatingPasswordEncoder 생성 (기본 : bcrypt)
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository userRepository) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/check-name","/api/auth/refresh","/api/home/*").permitAll()  // 로그인, 회원가입, 닉네임 확인은 인증 없이 접근 가능
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JWTFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json; charset=UTF-8");
                        response.getWriter().write("{\"statusCode\":401,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}");
                    })
            );

        return http.build();
    }
}
