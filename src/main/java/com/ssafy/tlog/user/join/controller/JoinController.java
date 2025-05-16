package com.ssafy.tlog.user.join.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.user.join.dto.JoinDtoRequest;
import com.ssafy.tlog.user.join.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class JoinController {
    private final JoinService joinService;

    // 닉네임 중복 확인
    @GetMapping("/check-name")
    public ResponseEntity<ResponseWrapper<Void>> checkNickname (@RequestParam String nickname) {
        joinService.checkNickname(nickname);
        return ApiResponse.success(HttpStatus.OK, "사용 가능한 닉네임 입니다.");
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<Void>> join(@RequestBody JoinDtoRequest joinDtoRequest) {
        joinService.join(joinDtoRequest);
        return ApiResponse.success(HttpStatus.CREATED, "회원가입 및 로그인이 성공적으로 완료되었습니다.");
    }
}
