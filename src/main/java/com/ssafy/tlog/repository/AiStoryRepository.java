package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.AiStory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface AiStoryRepository extends JpaRepository<AiStory, Integer> {
    boolean existsByTripIdAndUserId(Integer tripId, int userId);
    Optional<AiStory> findByTripIdAndUserId(int tripId, int userId);

    @Modifying
    @Transactional
    void deleteByTripId(int tripId);

    @Modifying
    @Transactional
    void deleteByTripIdAndUserId(int tripId, int userId);
}
