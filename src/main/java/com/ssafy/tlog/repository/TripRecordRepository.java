package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.TripRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TripRecordRepository extends JpaRepository<TripRecord, Integer> {
    boolean existsByTripIdAndUserId(Integer tripId, int userId);

    List<TripRecord> findAllByTripIdAndUserIdOrderByDay(int tripId, int userId);

    Optional<TripRecord> findByTripIdAndUserIdAndDay(int tripId, int userId, int day);

    @Modifying
    @Query("DELETE FROM TripRecord tr WHERE tr.tripId = :tripId AND tr.userId = :userId AND tr.day > :maxDay")
    void deleteExcessRecords(int tripId, int userId, int maxDay);

    void deleteByTripId(int tripId);

    void deleteByTripIdAndUserId(int tripId, int userId);
}
