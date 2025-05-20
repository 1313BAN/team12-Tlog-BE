package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.TripRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRecordRepository extends JpaRepository<TripRecord, Integer> {
    boolean existsByTripIdAndUserId(Integer tripId, int userId);
}
