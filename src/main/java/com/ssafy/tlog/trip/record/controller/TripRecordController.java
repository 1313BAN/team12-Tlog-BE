package com.ssafy.tlog.trip.record.controller;

import com.ssafy.tlog.trip.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripRecordController {
    private final RecordService recordService;

//    @GetMapping("/record")
//    public ResponseEntity<?> getTripRecordList(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        List<TripRecordListResponseDto> tripRedcordList = recordService.getTripRecordListByUser(userDetails.getUserId());
//        return ApiResponse.success(HttpStatus.OK, "여행 기록 리스트 조회에 성공했습니다.", tripRedcordList);
//    }
}
