package com.ssafy.tlog.auth.service;

import com.ssafy.tlog.auth.dto.JoinDtoRequest;
import com.ssafy.tlog.config.jwt.JWTUtil;
import com.ssafy.tlog.entity.Refresh;
import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.NicknameConflictException;
import com.ssafy.tlog.exception.custom.SocialIdConflictException;
import com.ssafy.tlog.repository.RefreshRepository;
import com.ssafy.tlog.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;

    private long accessExpiration = 3600000; // 1시간
    private long refreshExpiration = 604800000; // 7일

    // 닉네임 중복 확인
    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
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
    @Transactional
    public HttpHeaders generateAuthTokens(User user, HttpServletResponse response) {
        int userId = user.getUserId();
        String socialId = user.getSocialId();
        String nickname = user.getNickname();
        String role = user.getRole();

        String accessToken = jwtUtil.createJwt("access", userId, socialId, nickname, role, accessExpiration);
        String refreshToken = jwtUtil.createJwt("refresh", userId, socialId, nickname, role, refreshExpiration);

        // DB에 리프레시 토큰 저장
        saveRefreshToken(userId, refreshToken);

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

    // 리프레시 토큰을 DB에 저장하는 메서드
    private void saveRefreshToken(int userId, String refreshToken) {
        // 밀리초(refreshExpiration)를 LocalDateTime으로 변환
        LocalDateTime expiryDate = LocalDateTime.ofInstant(
                Instant.now().plusMillis(refreshExpiration),
                ZoneId.systemDefault()
        );

        // 기존 토큰이 있는지 확인하고, 있으면 업데이트, 없으면 새로 생성
        Refresh refreshEntity = refreshRepository.findById(userId)
                .orElse(new Refresh());

        // 리프레시 토큰 정보 설정
        refreshEntity.setUserId(userId);
        refreshEntity.setRefresh(refreshToken);
        refreshEntity.setExpiryDate(expiryDate);

        // 저장
        refreshRepository.save(refreshEntity);
    }

    // 쿠키에서 refresh 토큰 추출
    public String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean validateRefreshToken(String refreshToken) {
        // refresh가 DB에 존재하는지 확인
        Optional<Refresh> refreshOpt = refreshRepository.findByRefresh(refreshToken);
        if (refreshOpt.isEmpty()) {
            return false;
        }

        // 토큰 만료 시간 확인
        Refresh refresh = refreshOpt.get();
        return LocalDateTime.now().isBefore(refresh.getExpiryDate());
    }

    // refresh 토큰으로부터 사용자 ID를 조회
    public int getUserIdByRefreshToken(String refreshToken) {
        return refreshRepository.findByRefresh(refreshToken)
                .map(Refresh::getUserId)
                .orElse(-1);
    }
}
