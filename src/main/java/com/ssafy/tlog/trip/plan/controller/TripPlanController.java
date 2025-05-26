package com.ssafy.tlog.trip.plan.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.trip.plan.dto.TripPlanDetailResponseDto;
import com.ssafy.tlog.trip.plan.dto.TripPlanRequestDto;
import com.ssafy.tlog.trip.plan.dto.TripPlanResponseDto;
import com.ssafy.tlog.trip.plan.service.TripPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{tripId}/plan")
    public ResponseEntity<ResponseWrapper<TripPlanDetailResponseDto>> getTripPlanDetail(
            @PathVariable int tripId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TripPlanDetailResponseDto responseDto = tripPlanService.getTripPlanDetail(tripId, userDetails.getUser());
        return ApiResponse.success(HttpStatus.OK, "여행 계획 조회가 성공적으로 완료되었습니다.", responseDto);
    }

}
