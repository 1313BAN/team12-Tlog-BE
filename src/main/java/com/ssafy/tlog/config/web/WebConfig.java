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
        // API 경로에 대한 CORS 설정
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "https://team12-tlog-fe.vercel.app",
                        "https://t-log.kro.kr"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);

        // 이미지 경로에 대한 CORS 설정 (중복 제거 및 통합)
        registry.addMapping("/images/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "https://team12-tlog-fe.vercel.app",
                        "https://t-log.kro.kr"
                )
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
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