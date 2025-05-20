package com.ssafy.tlog.trip.record.service;

import com.ssafy.tlog.entity.Trip;
import com.ssafy.tlog.entity.TripParticipant;
import com.ssafy.tlog.repository.AiStoryRepository;
import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.TripRecordRepository;
import com.ssafy.tlog.repository.TripRepository;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto.TripDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto.TripInfoDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final TripRepository tripRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripRecordRepository tripRecordRepository;
    private final AiStoryRepository aiStoryRepository;

    public TripRecordListResponseDto getTripRecordListByUser(int userId) {
        // 사용자가 참여한 여행 ID 목록 조회
        List<TripParticipant> participants = tripParticipantRepository.findAllByUserId(userId);
        List<Integer> tripIds = participants.stream().map(TripParticipant::getTripId).collect(Collectors.toList());

        // 여행 기본 정보 조회
        List<Trip> trips = tripRepository.findAllByTripIdIn(tripIds);

        // 각 여행별 참여자 목록 조회(현재 사용자 제외)
        Map<Integer, List<Integer>> tripParticipantsMap = getTripParticipantsMap(tripIds, userId);

        // 각 여행 별 기록 저장 여부 확인
        Map<Integer, Boolean> hasStep1Map = getHasStep1Map(tripIds, userId);

        // 각 여행별 AI 생성 저장 여부 확인
        Map<Integer, Boolean> hasStep2Map = getHasStep2Map(tripIds, userId);

        // 응답 생성
        List<TripInfoDto> tripInfoDtos = createTripInfoDtos(trips, tripParticipantsMap, hasStep1Map, hasStep2Map);

        return TripRecordListResponseDto.builder().trips(tripInfoDtos).build();
    }

    // 각 여행별 참여자 목록 조회(현재 사용자 제외)
    private Map<Integer, List<Integer>> getTripParticipantsMap(List<Integer> tripIds, int userId) {
        // 모든 여행의 참여자 정보 조회
        List<TripParticipant> allParticipants = tripParticipantRepository.findAllByTripIdIn(tripIds);

        // 여행 ID를 키로, 참여자 ID 리스트를 값으로 하는 맵 생성
        Map<Integer, List<Integer>> participantsMap = new HashMap<>();

        // 각 여행별로 참여자 목록 구성 (현재 사용자 제외)
        for (TripParticipant participant : allParticipants) {
            if (participant.getUserId() != userId) {  // 현재 사용자 제외
                participantsMap
                        .computeIfAbsent(participant.getTripId(), k -> new ArrayList<>())
                        .add(participant.getUserId());
            }
        }

        // 모든 여행 ID에 대해 맵 항목 생성 (참여자가 없는 경우 빈 리스트)
        for (Integer tripId : tripIds) {
            participantsMap.putIfAbsent(tripId, new ArrayList<>());
        }

        return participantsMap;
    }

    private Map<Integer, Boolean> getHasStep1Map(List<Integer> tripIds, int userId) {
        Map<Integer, Boolean> hasStep1Map = new HashMap<>();

        // 각 여행 ID에 대해 기록(Step1) 존재 여부 확인
        for (Integer tripId : tripIds) {
            boolean hasStep1 = tripRecordRepository.existsByTripIdAndUserId(tripId, userId);
            hasStep1Map.put(tripId, hasStep1);
        }

        return hasStep1Map;
    }

    private Map<Integer, Boolean> getHasStep2Map(List<Integer> tripIds, int userId) {
        Map<Integer, Boolean> hasStep2Map = new HashMap<>();

        // 각 여행 ID에 대해 AI 기록(Step2) 존재 여부 확인
        for (Integer tripId : tripIds) {
            boolean hasStep1 = aiStoryRepository.existsByTripIdAndUserId(tripId, userId);
            hasStep2Map.put(tripId, hasStep1);
        }

        return hasStep2Map;
    }

    private List<TripInfoDto> createTripInfoDtos(List<Trip> trips, Map<Integer, List<Integer>> tripParticipantsMap,
                                                 Map<Integer, Boolean> hasStep1Map, Map<Integer, Boolean> hasStep2Map) {
        return trips.stream()
                .map(trip -> {
                    // Trip 엔티티를 TripDto로 변환
                    TripDto tripDto = TripDto.builder()
                            .title(trip.getTitle())
                            .createdAt(trip.getCreateAt())
                            .startDate(trip.getStartDate())
                            .endDate(trip.getEndDate())
                            .build();

                    // TripInfoDto 생성
                    return TripInfoDto.builder()
                            .trip(tripDto)
                            .tripParticipant(tripParticipantsMap.get(trip.getTripId()))
                            .hasStep1(hasStep1Map.getOrDefault(trip.getTripId(), false))
                            .hasStep2(hasStep2Map.getOrDefault(trip.getTripId(), false))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
