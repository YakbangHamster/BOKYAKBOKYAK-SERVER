package com.yakbang.server.repository;

import com.yakbang.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(Long userId);
    User findByIdentity(String identity);
    User findByUsername(String username);
}
