package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.AddAlarmRequest;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.entity.Alarm;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.AlarmRepository;
import com.yakbang.server.repository.MedicineRepository;
import com.yakbang.server.repository.UserRepository;
import com.yakbang.server.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final AlarmRepository alarmRepository;

    // 알림 등록
    public ResponseEntity<DefaultResponse> addAlarm(Long userId, AddAlarmRequest request) {
        User user = userRepository.findByUserId(userId);
        Medicine medicine = medicineRepository.findBySerial(request.serial());

        String startDate = request.startDate();
        if (startDate == null) startDate = LocalDate.now().toString();
        Alarm alarm = Alarm.create(user, medicine, request.time(), request.schedule(), startDate, request.endDate());
        alarmRepository.save(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 등록 성공"),
                HttpStatus.OK);
    }
}
