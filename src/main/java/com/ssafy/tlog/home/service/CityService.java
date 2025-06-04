package com.ssafy.tlog.home.service;

import com.ssafy.tlog.entity.City;
import com.ssafy.tlog.home.dto.CityResponseDto;
import com.ssafy.tlog.home.dto.CityResponseDto.CityDto;
import com.ssafy.tlog.repository.CityRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    @Transactional(readOnly = true)
    public CityResponseDto getCities(String name) {
        // cities 변수 초기화
        List<City> cities;

        if (name != null && !name.trim().isEmpty()) {
            cities = cityRepository.findByCityKoContainingOrCityEnContainingIgnoreCase(name, name);
        } else {
            cities = cityRepository.findAll();
            log.warn(cities.toString());
        }

        // 변환 작업
        List<CityDto> cityDtos = cities.stream()
                .map(city -> CityDto.builder()
                        .cityId(city.getCityId())
                        .cityEn(city.getCityEn())
                        .cityKo(city.getCityKo())
                        .build())
                .collect(Collectors.toList());

        // 결과 반환
        return CityResponseDto.builder()
                .cities(cityDtos)
                .build();
    }
}
