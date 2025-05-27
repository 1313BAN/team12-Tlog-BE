package com.ssafy.tlog.config.security;

import com.ssafy.tlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // socialId 기반으로 UserDetails 객체 반환
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
        return userRepository.findBySocialId(socialId)
                .map(CustomUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));
    }
}
