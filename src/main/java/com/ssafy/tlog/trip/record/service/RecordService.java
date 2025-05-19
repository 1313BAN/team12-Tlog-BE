package com.ssafy.tlog.trip.record.service;

import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.PlanRepository;
import com.ssafy.tlog.repository.RecordRepository;
import com.ssafy.tlog.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final TripRepository tripRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final PlanRepository planRepository;
    private final RecordRepository recordRepository;

}
