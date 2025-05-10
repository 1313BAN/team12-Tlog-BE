package com.ssafy.tlog.user.join.controller;

import com.ssafy.tlog.user.join.dto.JoinDto;
import com.ssafy.tlog.user.join.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class JoinController {
    private final JoinService joinService;

    // 회원가입
    @PostMapping
    public ResponseEntity<String> join(JoinDto joinDto) {
        joinService.join(joinDto);
        return ResponseEntity.ok().build();
    }
}
