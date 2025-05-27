package com.ssafy.tlog.trip.lock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditLockResponse {
    private boolean success;
    private String message;
    private Integer currentOwner;
    private Integer heartbeatInterval; // seconds
}
