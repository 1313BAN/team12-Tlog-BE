package com.ssafy.tlog.trip.record.dto;

import java.time.LocalDateTime;
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
public class TripRecordDetailResponseDto {
    private TripDto trip;
    private List<Integer> tripParticipant;
    private boolean hasStep1;
    private boolean hasStep2;
    private List<TripRecordDto> tripRecords;
    private String aiStoryContent;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripDto {
        private String title;
        private LocalDateTime createdAt;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripRecordDto {
        private int day;
        private LocalDateTime date;
        private String memo;
    }
}
