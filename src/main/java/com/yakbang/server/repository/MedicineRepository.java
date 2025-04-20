package com.yakbang.server.repository;

import com.yakbang.server.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Medicine findBySerial(String serial);
    Medicine findByName(String name);
    Medicine findByNameContaining(String name);
}
