package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Integer> {
}
