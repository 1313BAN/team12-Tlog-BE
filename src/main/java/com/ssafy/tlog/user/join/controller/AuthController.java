package com.ssafy.tlog.user.join.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.InvalidUserException;
import com.ssafy.tlog.user.join.dto.JoinDtoRequest;
import com.ssafy.tlog.user.join.dto.LoginRequest;
import com.ssafy.tlog.user.join.service.AuthService;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                                       HttpServletResponse response) {
        try {
            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getSocialId(), ""));
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();

            // 토큰 생성 및 쿠키 설정
            HttpHeaders headers = authService.generateAuthTokens(user, response);

            return ApiResponse.success(HttpStatus.OK, headers, "로그인이 성공적으로 처리되었습니다.", null);
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
    public ResponseEntity<ResponseWrapper<Void>> join(@Valid @RequestBody JoinDtoRequest joinDtoRequest, HttpServletResponse response) {
        User user = authService.join(joinDtoRequest);
        HttpHeaders headers = authService.generateAuthTokens(user, response);
        return ApiResponse.success(HttpStatus.CREATED, headers,"회원가입 및 로그인이 성공적으로 완료되었습니다.");
    }


}
