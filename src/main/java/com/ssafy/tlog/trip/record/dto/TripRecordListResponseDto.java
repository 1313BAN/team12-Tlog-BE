package com.ssafy.tlog.trip.record.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripRecordListResponseDto {

    private TripDto trip;
    private List<Integer> invitedUsers;
    private boolean hasStep1;
    private boolean hasStep2;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripDto {
        private int tripId;
        private String title;
        private LocalDateTime createdAt;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}
