package com.yakbang.server.repository;

import com.yakbang.server.entity.RefreshToken;
import com.yakbang.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
    void deleteByUserId(Long userId);
}
