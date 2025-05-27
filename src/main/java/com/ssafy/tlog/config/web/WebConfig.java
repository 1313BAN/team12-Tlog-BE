package com.ssafy.tlog.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:/uploads/images/}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 또는 모든 요청이라면 "/**"
                .allowedOrigins("http://localhost:5173", "http://localhost:5174") // 개발 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization") // Authorization 헤더 노출 설정 추가
                .allowCredentials(true); // 쿠키를 주고받을 경우

        // 이미지 경로에 대한 CORS 설정 추가
        registry.addMapping("/images/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false); // 이미지는 credentials 불필요
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 이미지 파일을 정적 리소스로 서빙
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600) // 1시간 캐시
                .resourceChain(true);

        // 디버깅을 위한 로그
        System.out.println("이미지 리소스 핸들러 설정 완료: /images/** -> " + uploadPath);
    }
}
