package com.ssafy.tlog.trip.record.service;

import com.ssafy.tlog.entity.AiStory;
import com.ssafy.tlog.entity.Trip;
import com.ssafy.tlog.entity.TripParticipant;
import com.ssafy.tlog.entity.TripPlan;
import com.ssafy.tlog.entity.TripRecord;
import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.InvalidUserException;
import com.ssafy.tlog.exception.custom.ResourceNotFoundException;
import com.ssafy.tlog.repository.AiStoryRepository;
import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.TripPlanRepository;
import com.ssafy.tlog.repository.TripRecordRepository;
import com.ssafy.tlog.repository.TripRepository;
import com.ssafy.tlog.repository.UserRepository;
import com.ssafy.tlog.trip.record.dto.TripRecordDetailResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto.TripDto;
import com.ssafy.tlog.trip.record.dto.TripRecordListResponseDto.TripInfoDto;
import com.ssafy.tlog.trip.record.dto.TripRecordResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordSaveRequestDto;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final UserRepository userRepository;

    @Transactional
    public TripRecordListResponseDto getTripRecordListByUser(int userId) {
        // 1. 사용자가 참여한 여행 ID 목록 조회
        List<Integer> tripIds = getUserTripIds(userId);
        if (tripIds.isEmpty()) {
            return TripRecordListResponseDto.builder().trips(Collections.emptyList()).build();
        }

        // 2. 여행 기본 정보 조회
        List<Trip> trips = tripRepository.findAllByTripIdInOrderByTripIdDesc(tripIds);

        // 3. 여행 관련 정보 맵 조회 (참여자, 단계별 상태)
        Map<Integer, List<String>> tripParticipantsMap = getTripParticipantsMap(tripIds, userId);
        Map<Integer, Boolean> hasStep1Map = getHasStep1Map(tripIds, userId);
        Map<Integer, Boolean> hasStep2Map = getHasStep2Map(tripIds, userId);

        // 4. 응답 DTO 생성
        List<TripInfoDto> tripInfoDtos = createTripInfoDtos(trips, tripParticipantsMap, hasStep1Map, hasStep2Map);

        return TripRecordListResponseDto.builder().trips(tripInfoDtos).build();
    }

    @Transactional
    public TripRecordDetailResponseDto getTripRecordDetailByTripId(int userId, int tripId) {
        // 1. 여행 존재 여부 확인
        Trip trip = validateAndGetTrip(tripId);

        // 2. 사용자 접근 권한 확인
        validateUserAccess(tripId, userId);

        // 3. 여행 관련 정보 조회
        List<Integer> tripIds = List.of(tripId);
        Map<Integer, List<String>> participantsMap = getTripParticipantsMap(tripIds, userId);
        boolean hasStep1 = tripRecordRepository.existsByTripIdAndUserId(tripId, userId);
        boolean hasStep2 = aiStoryRepository.existsByTripIdAndUserId(tripId, userId);

        // 4. 여행 계획 조회
        List<TripRecordResponseDto> tripPlans = getTripPlans(tripId);

        // 5. 여행 기록 조회 및 동기화
        List<TripRecord> tripRecords = getTripRecordsWithSync(tripId, userId, trip);

        // 6. AI 스토리 조회
        String aiStoryContent = getAiStoryContent(tripId, userId, hasStep2);

        // 7. 응답 DTO 생성
        return buildDetailResponseDto(trip, participantsMap.get(tripId), hasStep1, hasStep2, tripPlans, tripRecords,
                aiStoryContent);
    }

    private List<TripRecordResponseDto> getTripPlans(int tripId) {
        List<TripPlan> allPlans = tripPlanRepository.findAllByTripIdOrderByDayAscPlanOrderAsc(tripId);

        // 날짜별로 계획 그룹화
        Map<Integer, List<TripPlan>> plansByDay = allPlans.stream()
                .collect(Collectors.groupingBy(TripPlan::getDay));

        // 날짜별 응답 DTO 생성
        return plansByDay.entrySet().stream()
                .map(entry -> {
                    int day = entry.getKey();
                    List<TripPlan> plansForDay = entry.getValue();

                    List<TripRecordResponseDto.PlanDetailDto> planDetails = plansForDay.stream()
                            .map(plan -> TripRecordResponseDto.PlanDetailDto.builder()
                                    .planId(plan.getPlanId())
//                                    .cityId(plan.getCityId())
                                    .placeId(plan.getPlaceId())
                                    .planOrder(plan.getPlanOrder())
                                    .latitude(plan.getLatitude())
                                    .longitude(plan.getLongitude())
                                    .memo(plan.getMemo())
                                    .placeName(plan.getPlaceName())
                                    .build())
                            .collect(Collectors.toList());

                    return TripRecordResponseDto.builder()
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

    // 여행 참여자 맵 조회 - 기존 메소드를 수정
    private Map<Integer, List<String>> getTripParticipantsMap(List<Integer> tripIds, int userId) {
        // 모든 여행의 참여자 정보 조회
        List<TripParticipant> allParticipants = tripParticipantRepository.findAllByTripIdIn(tripIds);

        // 여행 ID를 키로, 참여자 닉네임 리스트를 값으로 하는 맵 생성
        Map<Integer, List<String>> participantsMap = new HashMap<>();

        // 참여자 userId 목록 수집
        Map<Integer, List<Integer>> userIdsByTrip = new HashMap<>();
        for (TripParticipant participant : allParticipants) {
            if (participant.getUserId() != userId) {  // 현재 사용자 제외
                userIdsByTrip
                        .computeIfAbsent(participant.getTripId(), k -> new ArrayList<>())
                        .add(participant.getUserId());
            }
        }

        // userId를 닉네임으로 변환
        for (Map.Entry<Integer, List<Integer>> entry : userIdsByTrip.entrySet()) {
            int tripId = entry.getKey();
            List<Integer> userIds = entry.getValue();

            // userRepository에서 해당 userIds에 대응하는 유저 정보 조회
            List<User> users = userRepository.findAllById(userIds);

            // 닉네임 목록 생성
            List<String> nicknames = users.stream()
                    .map(User::getNickname)
                    .collect(Collectors.toList());

            participantsMap.put(tripId, nicknames);
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
                                                 Map<Integer, List<String>> tripParticipantsMap,
                                                 Map<Integer, Boolean> hasStep1Map,
                                                 Map<Integer, Boolean> hasStep2Map) {
        return trips.stream()
                .map(trip -> {
                    // Trip 엔티티를 TripDto로 변환
                    TripDto tripDto = TripDto.builder()
                            .tripId(trip.getTripId())
                            .title(trip.getTitle())
                            .cityId(trip.getCityId())
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
    private TripRecordDetailResponseDto buildDetailResponseDto(Trip trip, List<String> participants,
                                                               boolean hasStep1, boolean hasStep2,
                                                               List<TripRecordResponseDto> tripPlans, // 추가된 매개변수
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

    @Transactional
    public void saveTripRecords(int userId, int tripId, TripRecordSaveRequestDto requestDto) {
        // 1. 여행 존재 여부 확인
        Trip trip = validateAndGetTrip(tripId);

        // 2. 사용자 접근 권한 확인
        validateUserAccess(tripId, userId);

        // 3. 날짜별 기록 저장 처리
        for (TripRecordSaveRequestDto.RecordDto recordDto : requestDto.getRecords()) {

            // 기존 기록이 있는지 확인
            TripRecord existingRecord = tripRecordRepository
                    .findByTripIdAndUserIdAndDay(tripId, userId, recordDto.getDay())
                    .orElse(null);

            if (existingRecord != null) {
                // 기존 기록 업데이트
                existingRecord.setMemo(recordDto.getMemo());
                tripRecordRepository.save(existingRecord);
            } else {
                // 새 기록 생성
                TripRecord newRecord = new TripRecord();
                newRecord.setTripId(tripId);
                newRecord.setUserId(userId);
                newRecord.setDay(recordDto.getDay());
                newRecord.setMemo(recordDto.getMemo());
                tripRecordRepository.save(newRecord);
            }
        }
    }

    /**
     * 간단한 여행 기록 동기화 + 초과 기록 삭제
     */
    @Transactional
    public List<TripRecord> getTripRecordsWithSync(int tripId, int userId, Trip trip) {
        // 현재 여행 일수 계산
        int totalDays = (int) ChronoUnit.DAYS.between(
                trip.getStartDate().toLocalDate(),
                trip.getEndDate().toLocalDate()
        ) + 1;

        // 기존 기록 조회
        List<TripRecord> existingRecords = tripRecordRepository.findAllByTripIdAndUserIdOrderByDay(tripId, userId);

        // 초과된 day 기록들 삭제
        List<TripRecord> excessRecords = existingRecords.stream()
                .filter(record -> record.getDay() > totalDays)
                .collect(Collectors.toList());

        if (!excessRecords.isEmpty()) {
            System.out.println("삭제할 기록 수: " + excessRecords.size());
            tripRecordRepository.deleteExcessRecords(tripId, userId, totalDays);
        }

        // 결과 리스트 (totalDays 크기로 초기화)
        List<TripRecord> result = new ArrayList<>();

        // 1일차부터 마지막 일차까지 처리
        for (int day = 1; day <= totalDays; day++) {
            final int currentDay = day;
            // 해당 일차의 기존 기록 찾기 (삭제되지 않은 것만)
            TripRecord existingRecord = existingRecords.stream()
                    .filter(record -> record.getDay() == currentDay)
                    .findFirst()
                    .orElse(null);

            if (existingRecord != null) {
                // 기존 기록이 있으면 추가
                result.add(existingRecord);
            } else {
                // 기존 기록이 없으면 빈 기록 생성 (DB 저장 안함)
                TripRecord emptyRecord = new TripRecord();
                emptyRecord.setTripId(tripId);
                emptyRecord.setUserId(userId);
                emptyRecord.setDay(day);
                emptyRecord.setMemo("");
                result.add(emptyRecord);
            }
        }

        return result;
    }
}
