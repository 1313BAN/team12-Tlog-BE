package com.ssafy.tlog.user.join.service;

import com.ssafy.tlog.entity.User;
import com.ssafy.tlog.exception.custom.InvalidDataException;
import com.ssafy.tlog.repository.UserRepository;
import com.ssafy.tlog.user.join.dto.JoinDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // PasswordEncoder로 비밀번호 인코딩해야 됨

    // 회원가입
    public void join(JoinDto joinDto) {
        // 이미 있는 username인지 검증
        if(userRepository.existsByUsername(joinDto.getUsername())){
            // 400 -> 수정 필요
            throw new InvalidDataException("이미 사용 중인 아이디입니다.");
        }

        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword())); // 비밀번호 암호화 후 저장
        user.setRole("USER"); // USER로 기본 설정

        // userRepository를 통해 DB에 저장 -> JPA 기능
        userRepository.save(user);
    }
}
