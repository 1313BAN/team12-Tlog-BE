package com.ssafy.tlog.user.join.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "소셜 ID는 필수입니다.")
    private String socialId;
}