package com.yakbang.server.repository;

import com.yakbang.server.entity.Alarm;
import com.yakbang.server.entity.Medication;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Alarm findByMedication(Medication medication);
    List<Alarm> findAllByMedication_User(User user);
}
