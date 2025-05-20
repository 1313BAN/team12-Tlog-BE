package com.ssafy.tlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration.class})
public class TlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlogApplication.class, args);
    }

}
