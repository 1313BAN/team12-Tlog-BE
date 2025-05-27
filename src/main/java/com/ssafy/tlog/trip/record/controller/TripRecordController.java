package com.ssafy.tlog.trip.record.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.trip.record.dto.TripRecordDetailResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordSaveRequestDto;
import com.ssafy.tlog.trip.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripRecordController {
    private final RecordService recordService;

    @GetMapping("/record")
    public ResponseEntity<ResponseWrapper<TripRecordListResponseDto>> getTripRecordList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TripRecordListResponseDto tripRecordListResponseDto = recordService.getTripRecordListByUser(
                userDetails.getUserId());
        return ApiResponse.success(HttpStatus.OK, "여행 기록 리스트 조회에 성공했습니다.", tripRecordListResponseDto);
    }

    @GetMapping("/{tripId}/record")
    public ResponseEntity<ResponseWrapper<TripRecordDetailResponseDto>> getTripRecordDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int tripId) {
        TripRecordDetailResponseDto tripRecordDetailResponseDto = recordService.getTripRecordDetailByTripId(
                userDetails.getUserId(), tripId);
        return ApiResponse.success(HttpStatus.OK, "여행 기록 상세 조회에 성공했습니다.", tripRecordDetailResponseDto);
    }

    @PostMapping("/{tripId}/record/save")
    public ResponseEntity<ResponseWrapper<Void>> saveTripRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable int tripId,
            @RequestBody TripRecordSaveRequestDto requestDto) {

        recordService.saveTripRecords(userDetails.getUserId(), tripId, requestDto);

        return ApiResponse.success(HttpStatus.OK, "여행 기록이 성공적으로 저장되었습니다.");
    }
}
