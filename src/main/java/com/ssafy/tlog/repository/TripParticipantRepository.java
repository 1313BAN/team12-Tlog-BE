package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.TripParticipant;
import com.ssafy.tlog.entity.TripParticipantId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, TripParticipantId> {

    List<TripParticipant> findAllByUserId(int userId);
    List<TripParticipant> findAllByTripIdIn(List<Integer> tripIds);
    boolean existsByTripIdAndUserId(int tripId, int userId);
}
