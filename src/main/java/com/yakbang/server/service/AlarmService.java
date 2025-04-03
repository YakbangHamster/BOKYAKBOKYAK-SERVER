package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.AlarmRequest;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.dto.response.AlarmResponse;
import com.yakbang.server.entity.Alarm;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.AlarmRepository;
import com.yakbang.server.repository.MedicineRepository;
import com.yakbang.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final AlarmRepository alarmRepository;

    // 알림 등록
    public ResponseEntity<DefaultResponse> addAlarm(Long userId, AlarmRequest request) {
        User user = userRepository.findByUserId(userId);
        Medicine medicine = medicineRepository.findBySerial(request.serial());

        String startDate = request.startDate();
        if (startDate == null) startDate = LocalDate.now().toString();
        Alarm alarm = Alarm.create(user, medicine, request.time(), request.schedule(), startDate, request.endDate());
        alarmRepository.save(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 등록 성공"),
                HttpStatus.OK);
    }

    // 알림 수정
    public ResponseEntity<DefaultResponse> modifyAlarm(Long userId, AlarmRequest request) {
        User user = userRepository.findByUserId(userId);
        Medicine medicine = medicineRepository.findBySerial(request.serial());

        Alarm alarm = alarmRepository.findByUserAndMedicine(user, medicine);

        alarm.setTime(request.time());
        alarm.setSchedule(request.schedule());
        if (request.startDate() != null) alarm.setStartDate(request.startDate());
        if (request.endDate() != null) alarm.setEndDate(request.endDate());
        alarmRepository.save(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 수정 성공"),
                HttpStatus.OK);
    }

    // 등록 알림 조회
    public ResponseEntity<DefaultResponse> getAlarm(Long userId) {
        User user = userRepository.findByUserId(userId);

        List<AlarmResponse> alarmList = new ArrayList<>();
        List<Alarm> alarms = alarmRepository.findAllByUser(user);

        for (int i = 0; i < alarms.size(); i++) {
            Alarm alarm = alarms.get(i);

            alarmList.add(new AlarmResponse(alarm.getMedicine().getSerial(), alarm.getMedicine().getName(), alarm.getTime(), alarm.getSchedule(), alarm.getStartDate(), alarm.getEndDate()));
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 조회 성공", alarmList),
                HttpStatus.OK);
    }
}
