package com.ssafy.tlog.trip.lock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditStatusResponse {
    private boolean isLocked;
    private Integer currentOwner;
}