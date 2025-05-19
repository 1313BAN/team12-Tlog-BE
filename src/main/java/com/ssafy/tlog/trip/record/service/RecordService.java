package com.ssafy.tlog.trip.record.service;

import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.TripPlanRepository;
import com.ssafy.tlog.repository.TripRecordRepository;
import com.ssafy.tlog.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final TripRepository tripRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripPlanRepository tripPlanRepository;
    private final TripRecordRepository tripRecordRepository;

}
