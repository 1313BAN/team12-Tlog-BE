package com.ssafy.tlog.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 또는 모든 요청이라면 "/**"
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "https://team12-tlog-fe.vercel.app",
                        "https://t-log.kro.kr",  // 새 도메인 추가
                        "http://t-log.kro.kr"   // HTTP도 임시로 추가 (테스트용)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization") // Authorization 헤더 노출 설정 추가
                .allowCredentials(true); // 쿠키를 주고받을 경우

        // 이미지 경로에 대한 CORS 설정
        registry.addMapping("/images/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "https://team12-tlog-fe.vercel.app",
                        "https://t-log.kro.kr"  // 새 도메인 추가
                )
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
