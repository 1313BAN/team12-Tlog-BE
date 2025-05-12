package com.ssafy.tlog.config.jwt;

import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // user_id 추출
    
    // name 추출

    // role 추출

    // 만료 검증

    // 토큰 생성
}
