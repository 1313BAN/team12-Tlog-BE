package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.TripImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripImageRepository extends JpaRepository<TripImage, Integer> {
    List<TripImage> findAllByTripIdAndUserIdOrderByDay(int tripId, int userId);
    Optional<TripImage> findByTripIdAndUserIdAndDay(int tripId, int userId, int day);
    void deleteByTripIdAndUserIdAndDay(int tripId, int userId, int day);
}
