package com.yakbang.server.repository;

import com.yakbang.server.entity.UserCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<UserCondition, Long> {
}
