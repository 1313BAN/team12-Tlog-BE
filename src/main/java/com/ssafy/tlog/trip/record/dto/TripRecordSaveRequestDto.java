package com.ssafy.tlog.trip.record.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripRecordSaveRequestDto {
    private List<RecordDto> records;

    @Getter
    @Setter
    public static class RecordDto {
        private int day;
        private LocalDate date;
        private String memo;
    }
}
