package com.ssafy.tlog.trip.record.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.trip.record.dto.AiStoryResponseDto;
import com.ssafy.tlog.trip.record.service.AiStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class AiStoryController {

    private final AiStoryService aiStoryService;

    @PostMapping("/{tripId}/ai-story")
    public ResponseEntity<ResponseWrapper<AiStoryResponseDto>> generateAiStory(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int tripId) {

        AiStoryResponseDto aiStoryResponseDto = aiStoryService.generateAiStory(userDetails.getUserId(), tripId);

        return ApiResponse.success(HttpStatus.OK, "AI 스토리가 성공적으로 생성되었습니다.", aiStoryResponseDto
        );
    }
}
