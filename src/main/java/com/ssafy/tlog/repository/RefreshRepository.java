package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.Refresh;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<Refresh, Integer> {
    Optional<Refresh> findByRefresh(String refresh);
}
