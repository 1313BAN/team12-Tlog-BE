package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.TripPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripPlanRepository extends JpaRepository<TripPlan, Integer> {
    List<TripPlan> findAllByTripIdOrderByDayAscPlanOrderAsc(int tripId);
    boolean existsByTripId(int tripId);
}
