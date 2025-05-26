package com.ssafy.tlog.user.service;

import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.repository.UserRepository;
import com.ssafy.tlog.user.dto.UserCheckResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserCheckResponseDto checkUserByNickname(String nickname) {
        // 닉네임이 빈 문자열이거나 null인 경우 예외 처리
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }

        // 닉네임으로 유저 존재 여부 확인
        Optional<User> userOptional = userRepository.findByNickname(nickname.trim());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return UserCheckResponseDto.builder()
                    .exists(true)
                    .userId(user.getUserId())
                    .nickname(user.getNickname())
                    .build();
        } else {
            return UserCheckResponseDto.builder()
                    .exists(false)
                    .userId(null)
                    .nickname(null)
                    .build();
        }
    }
}