package com.ssafy.tlog.exception.global;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "에러 발생 시 응답")
public class ErrorResponse {
    @Schema(description = "HTTP 상태 코드")
    private int statusCode;
    @Schema(description = "예외 이름")
    private String errorCode;
    @Schema(description = "예외 메시지")
    private String message;
}
