package com.ssafy.tlog.trip.plan.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.trip.plan.dto.TripPlanRequestDto;
import com.ssafy.tlog.trip.plan.dto.TripPlanResponseDto;
import com.ssafy.tlog.trip.plan.service.TripPlanService;
import com.ssafy.tlog.trip.record.dto.TripRecordResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripPlanController {

    private final TripPlanService tripPlanService;

    @PostMapping("/plan")
    public ResponseEntity<ResponseWrapper<TripPlanResponseDto>> createTripPlan(
            @RequestBody TripPlanRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TripPlanResponseDto responseDto = tripPlanService.createTripPlan(requestDto, userDetails.getUser());
        return ApiResponse.success(HttpStatus.OK, "여행 계획이 성공적으로 저장되었습니다.",responseDto);
    }
}
