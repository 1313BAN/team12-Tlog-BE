package com.ssafy.tlog.config.security;

import com.ssafy.tlog.config.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationManager authenticationManager;

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Spring Security가 내부적으로 생성한 AuthenticationManager를 반환하여 빈으로 등록
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보호 비활성화
        http.csrf(csrf -> csrf.disable());
        // 폼 로그인 기능 비활성화
        http.formLogin(form -> form.disable());
        // HTTP 기본 인증 비활성화
        http.httpBasic(basic -> basic.disable());
        // 전체 URL 허용 -> 권한 부여 필요!!
        http.authorizeHttpRequests((auth) -> auth.requestMatchers("/").permitAll());
        //  로그인 필터 추가
        http.addFilterAt(new LoginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
