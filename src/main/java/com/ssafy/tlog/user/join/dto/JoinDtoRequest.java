package com.ssafy.tlog.user.join.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class JoinDtoRequest {
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2-20자 사이여야 합니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{2,20}$", message = "닉네임은 한글, 영문, 숫자, 특수문자 '_', '-'만 사용 가능합니다.")
    private String nickname;
    @NotBlank(message = "소셜 ID는 필수 입력값입니다.$")
    private String socialId;
}
