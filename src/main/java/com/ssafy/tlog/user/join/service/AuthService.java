package com.ssafy.tlog.user.join.service;

import com.ssafy.tlog.config.jwt.JWTUtil;
import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.NicknameConflictException;
import com.ssafy.tlog.exception.custom.SocialIdConflictException;
import com.ssafy.tlog.repository.UserRepository;
import com.ssafy.tlog.user.join.dto.JoinDtoRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    private long accessExpiration = 3600000; // 1시간
    private long refreshExpiration = 604800000; // 7일

    // 닉네임 중복 확인
    public void checkNickname(String nickname) {
        if(userRepository.existsByNickname(nickname)) {
            throw new NicknameConflictException("이미 사용 중인 닉네임입니다.");
        }
    }

    // 회원가입
    public User join(JoinDtoRequest joinDtoRequest) {
        // 닉네임 중복 체크
        checkNickname(joinDtoRequest.getNickname());

        // 소셜ID 중복 체크
        if (userRepository.existsBySocialId(joinDtoRequest.getSocialId())) {
            throw new SocialIdConflictException("이미 사용 중인 소셜 ID입니다.");
        }

        User user = new User();
        user.setNickname(joinDtoRequest.getNickname());
        user.setSocialId(joinDtoRequest.getSocialId());
        user.setRole("USER");

        return userRepository.save(user);
    }

    // 토큰 생성 및 쿠키 설정을 위한 공통 메서드
    public HttpHeaders generateAuthTokens(User user, HttpServletResponse response) {
        int userId = user.getUserId();
        String socialId = user.getSocialId();
        String nickname = user.getNickname();
        String role = user.getRole();

        String accessToken = jwtUtil.createJwt("access", userId, socialId, nickname, role, accessExpiration);
        String refreshToken = jwtUtil.createJwt("refresh", userId, socialId, nickname, role, refreshExpiration);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Cookie refreshCookie = new Cookie("refresh", refreshToken);
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshCookie.setPath("/api/auth");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setAttribute("SameSite", "Strict");
        response.addCookie(refreshCookie);

        return headers;
    }
}
