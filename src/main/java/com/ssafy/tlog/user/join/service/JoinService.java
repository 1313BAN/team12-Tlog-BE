package com.ssafy.tlog.user.join.service;

import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.InvalidDataException;
import com.ssafy.tlog.exception.custom.NicknameConflictException;
import com.ssafy.tlog.repository.UserRepository;
import com.ssafy.tlog.user.join.dto.JoinDtoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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

        User user = new User();
        user.setNickname(joinDtoRequest.getNickname());
        user.setSocialId(joinDtoRequest.getSocialId());  // socialId는 암호화하지 않음
        user.setRole("USER");

        userRepository.save(user);
    }
}
