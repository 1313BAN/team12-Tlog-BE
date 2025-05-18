package com.ssafy.tlog.user.join.service;

import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.InvalidUserException;
import com.ssafy.tlog.exception.custom.NicknameConflictException;
import com.ssafy.tlog.exception.custom.SocialIdConflictException;
import com.ssafy.tlog.repository.UserRepository;
import com.ssafy.tlog.user.join.dto.JoinDtoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final UserRepository userRepository;

    // 닉네임 중복 확인
    public void checkNickname(String nickname) {
        if(userRepository.existsByNickname(nickname)) {
            throw new NicknameConflictException("이미 사용 중인 닉네임입니다.");
        }
    }

    // 회원가입
    public void join(JoinDtoRequest joinDtoRequest) {
        // 닉네임 중복 체크
        checkNickname(joinDtoRequest.getNickname());

        // 소셜ID 중복 체크
        if (userRepository.existsBySocialId(joinDtoRequest.getSocialId())) {
            throw new SocialIdConflictException("이미 사용 중인 소셜 ID입니다.");
        }

        User user = new User();
        user.setNickname(joinDtoRequest.getNickname());
        user.setSocialId(joinDtoRequest.getSocialId());
        user.setRole("USER");

        userRepository.save(user);
    }
}
