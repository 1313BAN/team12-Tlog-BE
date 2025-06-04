package com.ssafy.tlog.home.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.home.dto.CityResponseDto;
import com.ssafy.tlog.home.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @GetMapping("/cities")
    public ResponseEntity<ResponseWrapper<CityResponseDto>> getCities(@RequestParam(required = false) String name) {
        CityResponseDto cityResponseDto = cityService.getCities(name);
        return ApiResponse.success(HttpStatus.OK, "도시 목록 조회에 성공했습니다.", cityResponseDto);
    }
}
