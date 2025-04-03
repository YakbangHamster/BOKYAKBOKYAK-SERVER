package com.yakbang.server.repository;

import com.yakbang.server.composite_key.AlarmKey;
import com.yakbang.server.entity.Alarm;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, AlarmKey> {
    Alarm findByUserAndMedicine(User user, Medicine medicine);
    List<Alarm> findAllByUser(User user);
}
