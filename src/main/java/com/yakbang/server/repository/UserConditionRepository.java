package com.yakbang.server.repository;

import com.yakbang.server.entity.UserCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConditionRepository extends JpaRepository<UserCondition, Long> {
    UserCondition findByConditionId(Long conditionId);
}
