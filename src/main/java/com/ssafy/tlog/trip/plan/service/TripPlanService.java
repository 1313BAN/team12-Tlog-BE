package com.ssafy.tlog.trip.plan.service;

import com.ssafy.tlog.entity.Trip;
import com.ssafy.tlog.entity.TripParticipant;
import com.ssafy.tlog.entity.TripPlan;
import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.TripPlanRepository;
import com.ssafy.tlog.repository.TripRepository;
import com.ssafy.tlog.trip.plan.dto.TripPlanDetailResponseDto;
import com.ssafy.tlog.trip.plan.dto.TripPlanRequestDto;
import com.ssafy.tlog.trip.plan.dto.TripPlanResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripPlanService {

    private final TripRepository tripRepository;
    private final TripPlanRepository tripPlanRepository;
    private final TripParticipantRepository tripParticipantRepository;

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

        // 4. DTO 변환
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

        return TripPlanDetailResponseDto.builder()
                .tripId(trip.getTripId())
                .cityId(trip.getCityId())
                .title(trip.getTitle())
                .createAt(trip.getCreateAt())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .plans(planDetails)
                .build();
    }

}
