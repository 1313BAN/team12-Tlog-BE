package com.ssafy.tlog.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {
    private int statusCode;
    private String message;
    private T data;

}
