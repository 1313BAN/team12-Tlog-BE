package com.ssafy.tlog.trip.record.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRecordResponseDto {
    private int day;
    private List<PlanDetailDto> plans;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanDetailDto {
        private int planId;
        private int cityId;
        private String placeId;
        private int planOrder;
        private double latitude;
        private double longitude;
        private String memo;
        private String placeName;
    }
}
