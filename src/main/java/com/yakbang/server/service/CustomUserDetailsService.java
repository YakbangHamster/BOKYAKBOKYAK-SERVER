package com.yakbang.server.service;

import com.yakbang.server.dto.etc.CustomUserDetails;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId = Long.valueOf(username);
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new UsernameNotFoundException("(토큰 오류) 해당 유저가 존재하지 않습니다.");
        }

        return new CustomUserDetails(username);
    }
}
