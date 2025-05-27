package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findBySocialId(String SocialId);
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    boolean existsBySocialId(String socialId);
}