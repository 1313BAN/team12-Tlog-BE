package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username); // 반환값이 null이 될 수 있는 경우, Optional 달아주는게 좋음
    // @Query 이용해서 커스텀 sql 달아줄 수 있음!
}
