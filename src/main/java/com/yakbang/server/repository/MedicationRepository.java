package com.yakbang.server.repository;

import com.yakbang.server.composite_key.MedicationKey;
import com.yakbang.server.entity.Medication;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, MedicationKey> {
    Medication findByUserAndMedicine(User user, Medicine medicine);
    List<Medication> findAllByUser(User user);
}
