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
    private final PasswordEncoder passwordEncoder; // PasswordEncoder로 비밀번호 인코딩해야 됨

    // 닉네임 중복 확인
    public void checkNickname(String nickname){
        // 이미 존재하는 닉네임인 경우
        if(userRepository.existsByNickname(nickname)){
            throw new NicknameConflictException("이미 사용 중인 닉네임입니다.");
        }
    }

    // 회원가입
    public void join(JoinDtoRequest joinDtoRequest) {
        // 이미 있는 username인지 검증
        if(userRepository.existsByUsername(joinDtoRequest.getUsername())){
            // 400 -> 수정 필요
            throw new InvalidDataException("이미 사용 중인 아이디입니다.");
        }

        User user = new User();
        user.setUsername(joinDtoRequest.getUsername());
        user.setPassword(passwordEncoder.encode(joinDtoRequest.getPassword())); // 비밀번호 암호화 후 저장
        user.setRole("USER"); // USER로 기본 설정

        // userRepository를 통해 DB에 저장 -> JPA 기능
        userRepository.save(user);
    }
}
