package com.ssafy.tlog.trip.lock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeartbeatResponse {
    private boolean success;
    private String message;
    private Integer nextHeartbeat; // seconds
    private Boolean shouldRestart;
}
