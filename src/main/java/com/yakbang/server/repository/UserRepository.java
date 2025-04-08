package com.yakbang.server.repository;

import com.yakbang.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByIdentity(String identity);
    User findByUserId(Long userId);
    User findByIdentity(String identity);
    User findByUsername(String username);
}
