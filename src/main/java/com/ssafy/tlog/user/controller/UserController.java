package com.ssafy.tlog.user.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.user.dto.UserCheckResponseDto;
import com.ssafy.tlog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<ResponseWrapper<UserCheckResponseDto>> checkUserExists(
            @RequestParam("nickname") String nickname
    ) {
        UserCheckResponseDto responseDto = userService.checkUserByNickname(nickname);
        return ApiResponse.success(HttpStatus.OK, "유저 조회가 완료되었습니다.", responseDto);
    }
}