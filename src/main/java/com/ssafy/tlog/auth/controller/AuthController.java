package com.ssafy.tlog.auth.controller;

import com.ssafy.tlog.auth.dto.JoinDtoRequest;
import com.ssafy.tlog.auth.dto.LoginRequest;
import com.ssafy.tlog.auth.service.AuthService;
import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.InvalidTokenException;
import com.ssafy.tlog.exception.custom.InvalidUserException;
import com.ssafy.tlog.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getSocialId(), ""));
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();

            // 토큰 생성 및 쿠키 설정
            HttpHeaders headers = authService.generateAuthTokens(user, response);

            return ApiResponse.success(HttpStatus.OK, headers, "로그인이 성공적으로 처리되었습니다.", user);
        } catch (Exception e) {
            throw new InvalidUserException("존재하지 않는 사용자입니다.");
        }
    }

    // 닉네임 중복 확인
    @GetMapping("/check-name")
    public ResponseEntity<ResponseWrapper<Void>> checkNickname(@RequestParam String nickname) {
        authService.checkNickname(nickname);
        return ApiResponse.success(HttpStatus.OK, "사용 가능한 닉네임 입니다.");
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<User>> join(@Valid @RequestBody JoinDtoRequest joinDtoRequest,
                                                      HttpServletResponse response) {
        User user = authService.join(joinDtoRequest);
        HttpHeaders headers = authService.generateAuthTokens(user, response);
        return ApiResponse.success(HttpStatus.CREATED, headers, "회원가입 및 로그인이 성공적으로 완료되었습니다.", user);
    }

    // access 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refresh 토큰 추출
        String refreshToken = authService.extractRefreshTokenFromCookies(request);
        if (refreshToken == null) {
            throw new InvalidTokenException("refresh 토큰이 쿠키에 없습니다.");
        }

        // 토큰 유효성 검증
        if (!authService.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 refresh 토큰입니다.");
        }

        // 리프레시 토큰으로 사용자 ID 조회
        int userId = authService.getUserIdByRefreshToken(refreshToken);
        if (userId == -1) {
            throw new InvalidTokenException("토큰에서 사용자 정보를 찾을 수 없습니다.");
        }

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserException("존재하지 않는 사용자입니다."));

        HttpHeaders headers = authService.generateAuthTokens(user, response);
        return ApiResponse.success(HttpStatus.OK, headers, "토큰이 성공적으로 갱신되었습니다.");
    }

    // logout
    @PostMapping("/logout")
    public ResponseEntity<ResponseWrapper<Void>> logout(HttpServletResponse response, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            // DB에서 리프레시 토큰 삭제
            authService.logout(userDetails.getUserId());
        }

        // 쿠키 삭제
        Cookie refreshCookie = new Cookie("refresh", null);
        refreshCookie.setMaxAge(0); // 즉시 만료
        refreshCookie.setPath("/api/auth");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        response.addCookie(refreshCookie);

        return ApiResponse.success(HttpStatus.OK, "로그아웃 되었습니다.");
    }
}
