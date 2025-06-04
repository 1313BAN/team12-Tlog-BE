package com.ssafy.tlog.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCheckResponseDto {
    private boolean exists;
    private Integer userId;
    private String nickname;
}