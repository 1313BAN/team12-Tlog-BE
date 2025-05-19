package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.AiStory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiStoryRepository extends JpaRepository<AiStory, Integer> {
    boolean existsByTripIdAndUserId(Integer tripId, int userId);
}
