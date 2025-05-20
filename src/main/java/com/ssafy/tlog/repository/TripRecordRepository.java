package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.TripRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRecordRepository extends JpaRepository<TripRecord, Integer> {
    boolean existsByTripIdAndUserId(Integer tripId, int userId);
    List<TripRecord> findAllByTripIdAndUserIdOrderByDay(int tripId, int userId);
}
