package com.ssafy.tlog.user.join.controller;

import com.ssafy.tlog.user.join.dto.JoinDtoRequest;
import com.ssafy.tlog.user.join.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/users")
public class JoinController {
    private final JoinService joinService;

    // 닉네임 중복 확인
    @GetMapping
    public ResponseEntity<String> checkNickname (@RequestParam String nickname) {
        joinService.checkNickname(nickname);
        return ResponseEntity.ok("사용 가능한 닉네임 입니다.");
    }

    // 회원가입
    @PostMapping
    public ResponseEntity<String> join(@RequestBody JoinDtoRequest joinDtoRequest) {
        joinService.join(joinDtoRequest);
        return ResponseEntity.ok().build();
    }
}
