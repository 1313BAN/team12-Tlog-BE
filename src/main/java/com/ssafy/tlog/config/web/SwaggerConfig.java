package com.ssafy.tlog.config.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Tlog API 명세서",
                description = "API 명세서",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    GroupedOpenApi memberOpenApi() {
        return GroupedOpenApi.builder()
                .group("All API")
                .pathsToMatch("/**")
                .build();
    }
}
