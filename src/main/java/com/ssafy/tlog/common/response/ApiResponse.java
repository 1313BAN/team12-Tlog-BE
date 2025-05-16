package com.ssafy.tlog.common.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {

    public static <T>ResponseEntity<ResponseWrapper<T>> success(HttpStatus status, String message, T data) {
        ResponseWrapper<T> wrapper = new ResponseWrapper<>(
                status.value(),
                message,
                data
        );
        return ResponseEntity.status(status).body(wrapper);
    }

    public static <T>ResponseEntity<ResponseWrapper<T>> success(HttpStatus status, HttpHeaders headers,String message, T data) {
        ResponseWrapper<T> wrapper = new ResponseWrapper<>(
                status.value(),
                message,
                data
        );
        return ResponseEntity.status(status).headers(headers).body(wrapper);
    }
}
