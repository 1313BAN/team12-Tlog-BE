package com.ssafy.tlog.repository;

import com.ssafy.tlog.entity.City;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findByCityKoContainingOrCityEnContainingIgnoreCase(String cityKo, String cityEn);
}
