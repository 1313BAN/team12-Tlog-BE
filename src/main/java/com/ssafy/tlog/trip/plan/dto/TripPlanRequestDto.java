package com.ssafy.tlog.trip.plan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class TripPlanRequestDto {

    private List<Integer> friendUserIds;
    private int cityId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PlaceDto> places;

    @Getter
    @NoArgsConstructor
    public static class PlaceDto {
        private String placeId;
        private String name;
        private Double latitude;
        private Double longitude;
        private int day;
        private int order;
        private int placeType; // "숙소" 또는 "명소"
    }
}
