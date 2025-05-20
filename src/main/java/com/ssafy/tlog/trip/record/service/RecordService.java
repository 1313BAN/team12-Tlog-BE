package com.ssafy.tlog.trip.record.service;

import com.ssafy.tlog.entity.AiStory;
import com.ssafy.tlog.entity.Trip;
import com.ssafy.tlog.entity.TripParticipant;
import com.ssafy.tlog.entity.TripPlan;
import com.ssafy.tlog.entity.TripRecord;
import com.ssafy.tlog.exception.custom.InvalidUserException;
import com.ssafy.tlog.exception.custom.ResourceNotFoundException;
import com.ssafy.tlog.repository.AiStoryRepository;
import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.TripPlanRepository;
import com.ssafy.tlog.repository.TripRecordRepository;
import com.ssafy.tlog.repository.TripRepository;
import com.ssafy.tlog.trip.record.dto.TripPlanResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordDetailResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto.TripDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto.TripInfoDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final TripRepository tripRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripRecordRepository tripRecordRepository;
    private final TripPlanRepository tripPlanRepository;
    private final AiStoryRepository aiStoryRepository;

    @Transactional(readOnly = true)
    public TripRecordListResponseDto getTripRecordListByUser(int userId) {
        // 1. 사용자가 참여한 여행 ID 목록 조회
        List<Integer> tripIds = getUserTripIds(userId);
        if (tripIds.isEmpty()) {
            return TripRecordListResponseDto.builder().trips(Collections.emptyList()).build();
        }

        // 2. 여행 기본 정보 조회
        List<Trip> trips = tripRepository.findAllByTripIdIn(tripIds);

        // 3. 여행 관련 정보 맵 조회 (참여자, 단계별 상태)
        Map<Integer, List<Integer>> tripParticipantsMap = getTripParticipantsMap(tripIds, userId);
        Map<Integer, Boolean> hasStep1Map = getHasStep1Map(tripIds, userId);
        Map<Integer, Boolean> hasStep2Map = getHasStep2Map(tripIds, userId);

        // 4. 응답 DTO 생성
        List<TripInfoDto> tripInfoDtos = createTripInfoDtos(trips, tripParticipantsMap, hasStep1Map, hasStep2Map);

        return TripRecordListResponseDto.builder().trips(tripInfoDtos).build();
    }

    @Transactional(readOnly = true)
    public TripRecordDetailResponseDto getTripRecordDetailByTripId(int userId, int tripId) {
        // 1. 여행 존재 여부 확인
        Trip trip = validateAndGetTrip(tripId);

        // 2. 사용자 접근 권한 확인
        validateUserAccess(tripId, userId);

        // 3. 여행 관련 정보 조회
        List<Integer> tripIds = List.of(tripId);
        Map<Integer, List<Integer>> participantsMap = getTripParticipantsMap(tripIds, userId);
        boolean hasStep1 = tripRecordRepository.existsByTripIdAndUserId(tripId, userId);
        boolean hasStep2 = aiStoryRepository.existsByTripIdAndUserId(tripId, userId);

        // 4. 여행 계획 조회
        List<TripPlanResponseDto> tripPlans = getTripPlans(tripId);

        // 5. 여행 기록 조회
        List<TripRecord> tripRecords = tripRecordRepository.findAllByTripIdAndUserIdOrderByDay(tripId, userId);

        // 6. AI 스토리 조회
        String aiStoryContent = getAiStoryContent(tripId, userId, hasStep2);

        // 7. 응답 DTO 생성
        return buildDetailResponseDto(trip, participantsMap.get(tripId), hasStep1, hasStep2, tripPlans, tripRecords, aiStoryContent);
    }

    private List<TripPlanResponseDto> getTripPlans(int tripId) {
        List<TripPlan> allPlans = tripPlanRepository.findAllByTripIdOrderByDayAscPlanOrderAsc(tripId);

        // 날짜별로 계획 그룹화
        Map<Integer, List<TripPlan>> plansByDay = allPlans.stream()
                .collect(Collectors.groupingBy(TripPlan::getDay));

        // 날짜별 응답 DTO 생성
        return plansByDay.entrySet().stream()
                .map(entry -> {
                    int day = entry.getKey();
                    List<TripPlan> plansForDay = entry.getValue();

                    List<TripPlanResponseDto.PlanDetailDto> planDetails = plansForDay.stream()
                            .map(plan -> TripPlanResponseDto.PlanDetailDto.builder()
                                    .planId(plan.getPlanId())
                                    .cityId(plan.getCityId())
                                    .placeId(plan.getPlaceId())
                                    .planOrder(plan.getPlanOrder())
                                    .latitude(plan.getLatitude())
                                    .longitude(plan.getLongitude())
                                    .memo(plan.getMemo())
                                    .build())
                            .collect(Collectors.toList());

                    return TripPlanResponseDto.builder()
                            .day(day)
                            .plans(planDetails)
                            .build();
                })
                .sorted((a, b) -> Integer.compare(a.getDay(), b.getDay())) // 날짜순 정렬
                .collect(Collectors.toList());
    }

    // 여행 기본 검증
    private Trip validateAndGetTrip(int tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("요청한 여행을 찾을 수 없습니다."));
    }

    // 사용자 접근 권한 검증
    private void validateUserAccess(int tripId, int userId) {
        boolean isParticipant = tripParticipantRepository.existsByTripIdAndUserId(tripId, userId);
        if (!isParticipant) {
            throw new InvalidUserException("해당 여행 기록에 접근 권한이 없습니다.");
        }
    }

    // 사용자의 여행 ID 목록 조회
    private List<Integer> getUserTripIds(int userId) {
        List<TripParticipant> participants = tripParticipantRepository.findAllByUserId(userId);
        return participants.stream()
                .map(TripParticipant::getTripId)
                .collect(Collectors.toList());
    }

    // 여행 참여자 맵 조회
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

    // Step1 상태 맵 조회
    private Map<Integer, Boolean> getHasStep1Map(List<Integer> tripIds, int userId) {
        Map<Integer, Boolean> hasStep1Map = new HashMap<>();

        // 각 여행 ID에 대해 기록(Step1) 존재 여부 확인
        for (Integer tripId : tripIds) {
            boolean hasStep1 = tripRecordRepository.existsByTripIdAndUserId(tripId, userId);
            hasStep1Map.put(tripId, hasStep1);
        }

        return hasStep1Map;
    }

    // Step2 상태 맵 조회
    private Map<Integer, Boolean> getHasStep2Map(List<Integer> tripIds, int userId) {
        Map<Integer, Boolean> hasStep2Map = new HashMap<>();

        // 각 여행 ID에 대해 AI 기록(Step2) 존재 여부 확인
        for (Integer tripId : tripIds) {
            boolean hasStep2 = aiStoryRepository.existsByTripIdAndUserId(tripId, userId);
            hasStep2Map.put(tripId, hasStep2);
        }

        return hasStep2Map;
    }

    // AI 스토리 내용 조회
    private String getAiStoryContent(int tripId, int userId, boolean hasStep2) {
        if (!hasStep2) {
            return null;
        }

        return aiStoryRepository.findByTripIdAndUserId(tripId, userId)
                .map(AiStory::getContent)
                .orElse(null);
    }

    // 목록 응답 DTO 생성
    private List<TripInfoDto> createTripInfoDtos(List<Trip> trips,
                                                 Map<Integer, List<Integer>> tripParticipantsMap,
                                                 Map<Integer, Boolean> hasStep1Map,
                                                 Map<Integer, Boolean> hasStep2Map) {
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

    // 상세 응답 DTO 생성
    private TripRecordDetailResponseDto buildDetailResponseDto(Trip trip, List<Integer> participants,
                                                               boolean hasStep1, boolean hasStep2,
                                                               List<TripPlanResponseDto> tripPlans, // 추가된 매개변수
                                                               List<TripRecord> tripRecords,
                                                               String aiStoryContent) {
        // TripDto 생성
        TripRecordDetailResponseDto.TripDto tripDto = TripRecordDetailResponseDto.TripDto.builder()
                .tripId(trip.getTripId()) // 추가
                .title(trip.getTitle())
                .createdAt(trip.getCreateAt())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .build();

        // 날짜별 기록 DTO 생성
        List<TripRecordDetailResponseDto.TripRecordDto> recordDtos = tripRecords.stream()
                .map(record -> {
                    // 여행 시작일로부터 day만큼 더하여 날짜 계산
                    LocalDateTime date = trip.getStartDate().plusDays(record.getDay() - 1);

                    return TripRecordDetailResponseDto.TripRecordDto.builder()
                            .recordId(record.getRecordId())
                            .day(record.getDay())
                            .date(date)
                            .memo(record.getMemo())
                            .build();
                })
                .collect(Collectors.toList());

        // 최종 응답 DTO 조합
        return TripRecordDetailResponseDto.builder()
                .trip(tripDto)
                .tripParticipant(participants)
                .hasStep1(hasStep1)
                .hasStep2(hasStep2)
                .tripPlans(tripPlans) // 추가
                .tripRecords(recordDtos)
                .aiStoryContent(aiStoryContent)
                .build();
    }

}
