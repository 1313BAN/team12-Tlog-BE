package com.ssafy.tlog.trip.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlanDetailResponseDto {

    // Trip 정보
    private int tripId;
    private int cityId;
    private String title;
    private LocalDateTime createAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 계획 장소들
    private List<PlanDetailDto> plans;
    // 참여자 정보
    private List<ParticipantDto> participants;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanDetailDto {
        private int planId;
        private String placeId;
        private String placeName;
        private Double latitude;
        private Double longitude;
        private int day;
        private int planOrder;
        private int placeTypeId;
        private String memo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParticipantDto {
        private int userId;
        private String nickname;
    }
}