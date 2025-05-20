package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.AiStory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiStoryRepository extends JpaRepository<AiStory, Integer> {
    boolean existsByTripIdAndUserId(Integer tripId, int userId);
    Optional<AiStory> findByTripIdAndUserId(int tripId, int userId);
}
