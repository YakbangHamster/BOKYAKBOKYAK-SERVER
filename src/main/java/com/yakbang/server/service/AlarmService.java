package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.AlarmRequest;
import com.yakbang.server.dto.request.SetAlarmRequest;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.dto.response.AlarmResponse;
import com.yakbang.server.entity.Alarm;
import com.yakbang.server.entity.Medication;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.AlarmRepository;
import com.yakbang.server.repository.MedicationRepository;
import com.yakbang.server.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final MedicineRepository medicineRepository;
    private final MedicationRepository medicationRepository;
    private final AlarmRepository alarmRepository;

    // 알림 등록
    public ResponseEntity<DefaultResponse> addAlarm(User user, AlarmRequest request) {
        Medicine medicine = medicineRepository.findBySerial(request.serial());
        if (medicine == null) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.NOT_FOUND, "알림을 등록할 약을 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND);
        }

        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);

        Alarm alarm = Alarm.create(medication, request.timeList());
        alarmRepository.save(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 등록 성공"),
                HttpStatus.OK);
    }

    // 알림 수정
    public ResponseEntity<DefaultResponse> modifyAlarm(User user, AlarmRequest request) {
        Medicine medicine = medicineRepository.findBySerial(request.serial());
        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);

        Alarm alarm = alarmRepository.findByMedication(medication);
        if (alarm == null) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.NOT_FOUND, "알림을 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND);
        }

        alarm.setTimeList(request.timeList());
        alarmRepository.save(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 수정 성공"),
                HttpStatus.OK);
    }

    // 알림 삭제
    public ResponseEntity<DefaultResponse> deleteAlarm(User user, String serial) {
        Medicine medicine = medicineRepository.findBySerial(serial);
        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);

        Alarm alarm = alarmRepository.findByMedication(medication);
        if (alarm == null) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.NOT_FOUND, "알림을 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND);
        }
        alarmRepository.delete(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 삭제 성공"),
                HttpStatus.OK);
    }

    // 알림 상태 변화
    public ResponseEntity<DefaultResponse> setAlarm(User user, SetAlarmRequest request) {
        Medicine medicine = medicineRepository.findByName(request.medicineName());
        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);
        Alarm alarm = alarmRepository.findByMedication(medication);

        alarm.setSetting(request.isSet());
        alarmRepository.save(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 상태 변화 성공"),
                HttpStatus.OK);
    }

    // 등록 알림 조회
    public ResponseEntity<DefaultResponse> getAlarm(User user) {
        List<AlarmResponse> alarmList = new ArrayList<>();
        List<Alarm> alarms = alarmRepository.findAllByMedication_User(user);

        for (int i = 0; i < alarms.size(); i++) {
            Alarm alarm = alarms.get(i);

            Medicine medicine = alarm.getMedication().getMedicine();
            alarmList.add(new AlarmResponse(medicine.getName(), alarm.getTimeList(), alarm.isSetting()));
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "알림 조회 성공", alarmList),
                HttpStatus.OK);
    }
}
