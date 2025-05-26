package com.ssafy.tlog.trip.plan.service;

import com.ssafy.tlog.entity.Trip;
import com.ssafy.tlog.entity.TripParticipant;
import com.ssafy.tlog.entity.TripPlan;
import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.TripPlanRepository;
import com.ssafy.tlog.repository.TripRepository;
import com.ssafy.tlog.repository.UserRepository;
import com.ssafy.tlog.trip.plan.dto.TripPlanDetailResponseDto;
import com.ssafy.tlog.trip.plan.dto.TripPlanRequestDto;
import com.ssafy.tlog.trip.plan.dto.TripPlanResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripPlanService {

    private final TripRepository tripRepository;
    private final TripPlanRepository tripPlanRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final UserRepository userRepository;

    @Transactional
    public TripPlanResponseDto createTripPlan(TripPlanRequestDto requestDto, User creator) {
        Trip trip = Trip.builder()
                .cityId(requestDto.getCityId())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .createAt(LocalDateTime.now())
                .title(requestDto.getTitle())
                .build();
        tripRepository.save(trip);

        // 1. 사용자 본인 추가
        TripParticipant creatorParticipant = TripParticipant.builder()
                .tripId(trip.getTripId())
                .userId(creator.getUserId())
                .build();
        tripParticipantRepository.save(creatorParticipant);

        // 2. 친구 추가
        requestDto.getFriendUserIds().forEach(friendId -> {
            TripParticipant friendParticipant = TripParticipant.builder()
                    .tripId(trip.getTripId())
                    .userId(friendId)
                    .build();
            tripParticipantRepository.save(friendParticipant);
        });

        for (TripPlanRequestDto.PlaceDto placeDto : requestDto.getPlaces()) {
            TripPlan plan = TripPlan.builder()
                    .tripId(trip.getTripId())
                    .placeName(placeDto.getName())
                    .placeId(placeDto.getPlaceId())
                    .latitude(placeDto.getLatitude())
                    .longitude(placeDto.getLongitude())
                    .day(placeDto.getDay())
                    .planOrder(placeDto.getOrder())
                    .placeTypeId(placeDto.getPlaceType())
                    .build();
            tripPlanRepository.save(plan);
        }

        return TripPlanResponseDto.builder()
                .tripId(trip.getTripId())
                .build();
    }

    public TripPlanDetailResponseDto getTripPlanDetail(int tripId, User user) {
        // 1. 해당 여행에 참여하는지 확인
        if (!tripParticipantRepository.existsByTripIdAndUserId(tripId, user.getUserId())) {
            throw new IllegalArgumentException("해당 여행에 참여하지 않은 사용자입니다.");
        }

        // 2. Trip 정보 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 여행입니다."));

        // 3. TripPlan 정보 조회 (day, planOrder 순으로 정렬)
        List<TripPlan> tripPlans = tripPlanRepository.findAllByTripIdOrderByDayAscPlanOrderAsc(tripId);

        // 4. 참여자 정보 조회
        List<TripParticipant> participants = tripParticipantRepository.findAllByTripId(tripId);

        // 5. 참여자의 User 정보 조회
        List<Integer> userIds = participants.stream()
                .map(TripParticipant::getUserId)
                .toList();

        List<User> participantUsers = userRepository.findAllById(userIds);

        // 6. DTO 변환
        List<TripPlanDetailResponseDto.PlanDetailDto> planDetails = tripPlans.stream()
                .map(plan -> TripPlanDetailResponseDto.PlanDetailDto.builder()
                        .planId(plan.getPlanId())
                        .placeId(plan.getPlaceId())
                        .placeName(plan.getPlaceName())
                        .latitude(plan.getLatitude())
                        .longitude(plan.getLongitude())
                        .day(plan.getDay())
                        .planOrder(plan.getPlanOrder())
                        .placeTypeId(plan.getPlaceTypeId())
                        .memo(plan.getMemo())
                        .build())
                .toList();

        List<TripPlanDetailResponseDto.ParticipantDto> participantDtos = participantUsers.stream()
                .map(participantUser -> TripPlanDetailResponseDto.ParticipantDto.builder()
                        .userId(participantUser.getUserId())
                        .nickname(participantUser.getNickname())
                        .build())
                .toList();

        return TripPlanDetailResponseDto.builder()
                .tripId(trip.getTripId())
                .cityId(trip.getCityId())
                .title(trip.getTitle())
                .createAt(trip.getCreateAt())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .plans(planDetails)
                .participants(participantDtos) // 참여자 정보 추가
                .build();
    }

    @Transactional
    public TripPlanResponseDto updateTripPlan(int tripId, TripPlanRequestDto requestDto, User user) {
        // 1. 여행 존재 여부 및 권한 확인
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 여행입니다."));

        if (!tripParticipantRepository.existsByTripIdAndUserId(tripId, user.getUserId())) {
            throw new IllegalArgumentException("해당 여행 계획을 수정할 권한이 없습니다.");
        }

        // 2. 여행 기본 정보 업데이트 (제목, 날짜 등)
        if (requestDto.getTitle() != null) {
            trip.setTitle(requestDto.getTitle());
        }
        if (requestDto.getStartDate() != null) {
            trip.setStartDate(requestDto.getStartDate());
        }
        if (requestDto.getEndDate() != null) {
            trip.setEndDate(requestDto.getEndDate());
        }
        if (requestDto.getCityId() != 0) {
            trip.setCityId(requestDto.getCityId());
        }
        tripRepository.save(trip);

        // 3. 기존 TripPlan들 삭제
        tripPlanRepository.deleteAllByTripId(tripId);

        // 4. 기존 참여자들 삭제 (본인 제외)
        tripParticipantRepository.deleteAllByTripIdExceptUser(tripId, user.getUserId());

        // 5. 새로운 친구들 추가
        if (requestDto.getFriendUserIds() != null && !requestDto.getFriendUserIds().isEmpty()) {
            // 현재 사용자 ID 제외 및 중복 제거
            List<Integer> friendIds = requestDto.getFriendUserIds().stream()
                    .filter(friendId -> !friendId.equals(user.getUserId())) // 현재 사용자 제외
                    .distinct() // 중복 제거
                    .collect(Collectors.toList());

            friendIds.forEach(friendId -> {
                TripParticipant friendParticipant = TripParticipant.builder()
                        .tripId(tripId)
                        .userId(friendId)
                        .build();
                tripParticipantRepository.save(friendParticipant);
            });
        }

        // 6. 새로운 장소들 추가
        if (requestDto.getPlaces() != null && !requestDto.getPlaces().isEmpty()) {
            for (TripPlanRequestDto.PlaceDto placeDto : requestDto.getPlaces()) {
                TripPlan plan = TripPlan.builder()
                        .tripId(tripId)
                        .placeName(placeDto.getName())
                        .placeId(placeDto.getPlaceId())
                        .latitude(placeDto.getLatitude())
                        .longitude(placeDto.getLongitude())
                        .day(placeDto.getDay())
                        .planOrder(placeDto.getOrder())
                        .placeTypeId(placeDto.getPlaceType())
                        .build();
                tripPlanRepository.save(plan);
            }
        }

        return TripPlanResponseDto.builder()
                .tripId(tripId)
                .build();
    }

}
