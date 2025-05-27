package com.ssafy.tlog.common.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse<T> {

    // 상태코드 + 메시지
    public static <T> ResponseEntity<ResponseWrapper<Void>> success(HttpStatus status, String message) {
        ResponseWrapper<Void> wrapper = new ResponseWrapper<>(
                status.value(),
                message,
                null
        );
        return ResponseEntity.status(status).body(wrapper);
    }

    // 상태코드 + 헤더 + 메시지
    public static <T> ResponseEntity<ResponseWrapper<Void>> success(HttpStatus status, HttpHeaders headers,String message) {
        ResponseWrapper<Void> wrapper = new ResponseWrapper<>(
                status.value(),
                message,
                null
        );
        return ResponseEntity.status(status).headers(headers).body(wrapper);
    }

    // 상태코드 + 메시지 + 데이터
    public static <T> ResponseEntity<ResponseWrapper<T>> success(HttpStatus status, String message, T data) {
        ResponseWrapper<T> wrapper = new ResponseWrapper<>(
                status.value(),
                message,
                data
        );
        return ResponseEntity.status(status).body(wrapper);
    }

    // 상태코드 + 헤더 + 메시지 + 데이터
    public static <T> ResponseEntity<ResponseWrapper<T>> success(HttpStatus status, HttpHeaders headers,String message, T data) {
        ResponseWrapper<T> wrapper = new ResponseWrapper<>(
                status.value(),
                message,
                data
        );
        return ResponseEntity.status(status).headers(headers).body(wrapper);
    }
}
