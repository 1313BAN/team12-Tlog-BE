package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.Trip;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Integer> {
    List<Trip> findAllByTripIdIn(List<Integer> tripIds);
}
