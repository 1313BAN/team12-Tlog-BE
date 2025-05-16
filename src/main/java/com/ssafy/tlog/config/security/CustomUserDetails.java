package com.ssafy.tlog.config.security;

import com.ssafy.tlog.entity.User;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    // User 객체를 반환하는 getter 생성
    public User getUser(){
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 사용자 역할에 ROLE_을 생략했음
        String role = "ROLE_"+user.getRole();
        // 역할을 SimpleGrantedAuthority 객체로 변환하여 목록에 추가
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    public int getUserId(){
        return user.getUserId();
    }

    @Override
    public String getUsername() { // social_id
        return user.getSocialId();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
