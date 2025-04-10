package com.yakbang.server.repository;

import com.yakbang.server.entity.User;
import com.yakbang.server.entity.UserCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserConditionRepository extends JpaRepository<UserCondition, Long> {
    UserCondition findByDate(LocalDate date);
    UserCondition findByUserAndDate(User user, LocalDate date);
    List<UserCondition> findAllByUser(User user);
}
