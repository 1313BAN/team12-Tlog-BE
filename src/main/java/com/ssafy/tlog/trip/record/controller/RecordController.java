package com.ssafy.tlog.trip.record.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.trip.record.dto.TripListResponseDto;
import com.ssafy.tlog.trip.record.service.RecordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class RecordController {
    private final RecordService recordService;

    @GetMapping("/record")
    public ResponseEntity<?> getTripRecordList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // List<TripListResponseDto> tripRedcordList = recordService.getTripRecordListByUser(userDetails.getUserId());
        return ApiResponse.success(HttpStatus.OK, "여행 기록 리스트 조회 성공");
    }
}
