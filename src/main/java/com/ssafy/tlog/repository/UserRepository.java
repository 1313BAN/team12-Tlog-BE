package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findBySocialId(String SocialId);
    boolean existsByNickname(String nickname);
}