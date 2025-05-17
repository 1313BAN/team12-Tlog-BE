package com.ssafy.tlog.user.join.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.jwt.JWTUtil;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.exception.global.ErrorResponse;
import com.ssafy.tlog.user.join.dto.JoinDtoRequest;
import com.ssafy.tlog.user.join.dto.LoginRequest;
import com.ssafy.tlog.user.join.service.JoinService;
import jakarta.servlet.http.Cookie;
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
    private final JWTUtil jwtUtil;
    private final JoinService joinService;
    private final AuthenticationManager authenticationManager;

    private long accessExpiration = 3600000; // 1시간
    private long refreshExpiration = 604800000; // 7일

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getSocialId(), "")
            );
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            int userId = customUserDetails.getUserId();
            String socialId = customUserDetails.getUsername();
            String nickname = customUserDetails.getUser().getNickname();
            String role = customUserDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

            String accessToken = jwtUtil.createJwt("access", userId, socialId, nickname, role, accessExpiration);
            String refreshToken = jwtUtil.createJwt("refresh", userId, socialId, nickname, role, refreshExpiration);

            // 헤더에 토큰 추가 - Bearer 접두사 유지 (헤더에서는 공백 허용됨)
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            // 쿠키에 refresh 토큰 추가 - Bearer 접두사 제거
            Cookie refreshCookie = new Cookie("refresh", refreshToken); // 공백 없이 토큰만 저장
            refreshCookie.setMaxAge(7*24*60*60);
            refreshCookie.setPath("/api/auth");
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true); // HTTPS에서만 전송
            refreshCookie.setAttribute("SameSite", "Strict"); // CSRF 방지
            response.addCookie(refreshCookie);

            return ApiResponse.success(HttpStatus.OK, headers, "로그인 성공", null);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse(
                    401,
                    "UNAUTHORIZED",
                    "로그인 실패: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // 닉네임 중복 확인
    @GetMapping("/check-name")
    public ResponseEntity<ResponseWrapper<Void>> checkNickname(@RequestParam String nickname) {
        joinService.checkNickname(nickname);
        return ApiResponse.success(HttpStatus.OK, "사용 가능한 닉네임 입니다.");
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<Void>> join(@Valid @RequestBody JoinDtoRequest joinDtoRequest) {
        joinService.join(joinDtoRequest);
        return ApiResponse.success(HttpStatus.CREATED, "회원가입 및 로그인이 성공적으로 완료되었습니다.");
    }
}
