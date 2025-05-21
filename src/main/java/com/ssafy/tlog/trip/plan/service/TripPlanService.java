package com.ssafy.tlog.trip.plan.service;

import com.ssafy.tlog.entity.*;
import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.TripPlanRepository;
import com.ssafy.tlog.repository.TripRepository;
import com.ssafy.tlog.trip.plan.dto.TripPlanRequestDto;
import com.ssafy.tlog.trip.plan.dto.TripPlanResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
