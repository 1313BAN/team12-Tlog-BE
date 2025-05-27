package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.TripParticipant;
import com.ssafy.tlog.entity.TripParticipantId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, TripParticipantId> {

    List<TripParticipant> findAllByUserId(int userId);
    List<TripParticipant> findAllByTripIdIn(List<Integer> tripIds);
    List<TripParticipant> findAllByTripId(int tripId);
    boolean existsByTripIdAndUserId(int tripId, int userId);

    @Modifying
    @Query("DELETE FROM TripParticipant tp WHERE tp.tripId = :tripId AND tp.userId != :userId")
    void deleteAllByTripIdExceptUser(int tripId, int userId);

    void deleteByTripIdAndUserId(int tripId, int userId);

    void deleteByTripId(int tripId);
}
