package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.AddMedicineRequest;
import com.yakbang.server.dto.request.DeleteMedicationRequest;
import com.yakbang.server.dto.request.MedicineDetailRequest;
import com.yakbang.server.dto.response.*;
import com.yakbang.server.entity.Alarm;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.Medication;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.AlarmRepository;
import com.yakbang.server.repository.MedicationRepository;
import com.yakbang.server.repository.MedicineRepository;
import com.yakbang.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final MedicationRepository medicationRepository;
    private final AlarmRepository alarmRepository;
    private final ChatService chatService;
    private final MedicineService medicineService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 약 검색
    public ResponseEntity<DefaultResponse> getMedicine(String medicineName, int page) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        List<MedicineResponse> response = medicineService.getSearchMedicine(medicineName, page);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 검색 성공", response),
                HttpStatus.OK);
    }

    // 약 등록
    public ResponseEntity<DefaultResponse> addMedicine(AddMedicineRequest request) {
        Medicine medicine = medicineRepository.findBySerial(request.serial());
        if (medicine == null) {
            medicine = Medicine.create(request.serial(), request.name(), request.image(), request.efficacy(), request.howToTake());
            medicineRepository.save(medicine);
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 등록 성공"),
                HttpStatus.OK);
    }

    // OCR 약 등록
    public ResponseEntity<DefaultResponse> addMedicineWithOCR(Long userId, String url) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String ocr = GoogleVisionOCR.execute(url);
        System.out.println("ocr 결과: " + ocr);

        String response = chatService.getChatGPT(ocr + "\n이 중에서 약 이름이랑 용량만 골라서 적어줘. 이미 적혀있는 부분까지만 적고, ;으로만 구분해줘");
        System.out.println("gpt 결과: " + response);

        // 복약 정보를 등록하기 위해 약을 등록하면서 serial을 받아옴
        List<String> serial = new ArrayList<>();
        // 약 새로 등록
        String[] medicines = response.split(";");
        for (int i = 0; i < medicines.length; i++) {
            serial.add(medicineService.addMedicine(medicines[i]));
        }

        // 복약 정보 등록
        for (int i = 0; i < serial.size(); i++) {
            User user = userRepository.findByUserId(userId);
            Medicine medicine = medicineRepository.findBySerial(serial.get(i));

            Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);
            if (medication == null) {
                medicationRepository.save(Medication.create(user, medicine));
            }
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 등록 성공"),
        HttpStatus.OK);
    }

    // 부작용 조회
    public ResponseEntity<DefaultResponse> searchSideEffect(String name) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String response = medicineService.getCaution(name);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "부작용 조회 성공", response),
                HttpStatus.OK);
    }

    // 등록 약 전체 조회
    public ResponseEntity<DefaultResponse> getAllMedication(User user, int page) {
        // 보유한 모든 복약 정보의 약 이름, 시작 날짜 반환
        List<AllMedicationResponse> response = new ArrayList<>();

        List<Medication> medication = medicationRepository.findAllByUser(user);
        int pageStart = page * 20;
        for (int i = pageStart; i < pageStart + 20; i++) {
            if (i >= medication.size()) break;

            LocalDate startDate = medication.get(i).getStartDate();
            String startDateString = null;

            if (startDate != null) startDateString = startDate.format(formatter);
            response.add(new AllMedicationResponse(medication.get(i).getMedicine().getName(), startDateString));
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 전체 조회 성공", response),
                HttpStatus.OK);
    }

    // 약 조회
    public ResponseEntity<DefaultResponse> getMedication(User user, String medicineName) {
        List<String> response = new ArrayList<>();

        Medicine medicine = medicineRepository.findByNameContaining(medicineName);
        if (medicine == null) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.NOT_FOUND, "해당 약을 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND);
        }

        response.add(medicine.getSerial());
        response.add(medicine.getName());
        response.add(medicine.getImage());
        response.add(medicine.getEfficacy());
        response.add(medicine.getHowToTake());

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 조회 성공", response),
                HttpStatus.OK);
    }

    // 레포트 조회
    public ResponseEntity<DefaultResponse> getReport(User user) {
        // 오늘 요일 구하기
        LocalDate date = LocalDate.now();
        int dayOfWeekNum = date.getDayOfWeek().getValue();

        // 레포트 시작 날짜, 끝 날짜 설정
        LocalDate startDate = date.minusDays(dayOfWeekNum - 1);
        LocalDate endDate = date.plusDays(7 - dayOfWeekNum);

        // 주간 복약 횟수, 약별 복약 기록, 기대 복약 개수
        int[] schedule = new int[7];
        Map<String, Integer> percentMap = new HashMap<>();
        int expected = 0;

        List<Medication> medications = medicationRepository.findAllByUser(user);
        for (Medication medication: medications) {
            List<LocalDate> takeRecord = medication.getTakeRecord();
            if (takeRecord.isEmpty()) continue;

            for (int i = 0; i < takeRecord.size(); i++) {
                LocalDate record = takeRecord.get(i);

                // startDate와 endDate 사이에 포함되면
                if ((record.isEqual(startDate) || record.isAfter(startDate)) && (record.isEqual(endDate) || record.isBefore(endDate))) {
                    // 주간 복약 횟수 추가
                    schedule[record.getDayOfWeek().getValue() - 1]++;
                    // 약별 복약 기록 추가
                    percentMap.merge(medication.getMedicine().getName(), 1, Integer::sum);
                }
            }

            // 기대 복약 개수를 구하기 위해 복약 기록의 startDate, endDate에 따라 반복문 범위 설정
            int expectedStartdate = startDate.getDayOfWeek().getValue() - 1;
            int expectedEndDate = 7;
            if (medication.getStartDate().isAfter(startDate)) expectedStartdate += (int) ChronoUnit.DAYS.between(medication.getStartDate(), startDate);
            if (medication.getEndDate().isBefore(endDate)) expectedEndDate -= (int) ChronoUnit.DAYS.between(medication.getEndDate(), endDate);

            // 복약 스케줄을 받아 해당하는 요일이 있으면 sum 증가, number 값을 곱해 해당 약의 한 주 전체 복약 개수 계산
            int sum = 0;
            List<Boolean> expectedSchedule = medication.getSchedule();
            for (int i = expectedStartdate; i < expectedEndDate; i++) {
                if (expectedSchedule.get(i)) sum++;
            }
            expected += sum * medication.getNumber();
        }

        // 스케줄 리스트로 변환
        List<Integer> scheduleInteger = new ArrayList<>();
        for (int i: schedule) scheduleInteger.add(schedule[i]);
    
        // 약별 복약 퍼센트
        List<ReportMedication> reportMedications = new ArrayList<>();
        int totalMedicine = percentMap.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        
        // 모든 키를 순회하면서 복약 퍼센트 계산
        for (String key: percentMap.keySet()) {
            reportMedications.add(new ReportMedication(key, (double) percentMap.get(key) / totalMedicine));
        }

        // 복약순응도 계산(실제 복약 개수 / 기대 복약 개수 * 100)
        double compliance = (double) totalMedicine / expected * 100;

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "레포트 조회 성공",
                new ReportResponse(user.getUsername(), startDate.format(formatter), endDate.format(formatter), scheduleInteger, reportMedications, compliance)),
                HttpStatus.OK);
    }

    // 복용약 상세 등록
    public ResponseEntity<DefaultResponse> addDetail(User user, MedicineDetailRequest request) {
        // 약 없으면(직접 입력이면) 등록
        Medicine medicine = medicineRepository.findByName(request.name());
        if (medicine == null) {
            medicine = Medicine.create("0", request.name(), "none", "none", "none");
            medicineRepository.save(medicine);
        }

        // 복약 상세 정보 추가
        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);
        if (medication == null) {
            medication = Medication.create(user, medicine);
        }
        medication.setSchedule(request.schedule());
        medication.setStartDate(LocalDate.parse(request.startDate(), formatter));
        medication.setEndDate(LocalDate.parse(request.endDate(), formatter));
        medication.setNumber(request.number());
        medicationRepository.save(medication);

        // 알림 설정
        Alarm alarm = Alarm.create(medication, request.time());
        alarmRepository.save(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "복약 상세 등록 성공"),
                HttpStatus.OK);
    }

    // 복용약 상세 수정
    public ResponseEntity<DefaultResponse> modifyDetail(User user, MedicineDetailRequest request) {
        Medicine medicine = medicineRepository.findByName(request.name());

        // 복약 상세 정보 수정
        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);
        if (request.schedule() != null) medication.setSchedule(request.schedule());
        if (request.startDate() != null) medication.setStartDate(LocalDate.parse(request.startDate(), formatter));
        if (request.endDate() != null) medication.setEndDate(LocalDate.parse(request.endDate(), formatter));
        if (request.number() != null) medication.setNumber(request.number());
        medicationRepository.save(medication);

        Alarm alarm = alarmRepository.findByMedication(medication);
        if (request.time() != null) alarm.setTimeList(request.time());
        alarmRepository.save(alarm);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "복약 상세 수정 성공"),
                HttpStatus.OK);
    }
    
    // 복용약 상세 조회
    public ResponseEntity<DefaultResponse> getDetail(User user, String medicineName) {
        Medicine medicine = medicineRepository.findByName(medicineName);
        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);
        Alarm alarm = alarmRepository.findByMedication(medication);

        MedicineDetailResponse response = new MedicineDetailResponse(user.getUsername(), medication.getSchedule(), medication.getStartDate().format(formatter), medication.getEndDate().format(formatter), medication.getNumber(), alarm.getTimeList());

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "복약 상세 조회 성공", response),
                HttpStatus.OK);
    }

    // 복용 확인 등록
    public ResponseEntity<DefaultResponse> addMedication(Long userId, String medicineName) {
        User user = userRepository.findByUserId(userId);
        Medicine medicine = medicineRepository.findByName(medicineName);

        // 복용 기록 확인하기
        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);

        // 복용 기록이 없다면
        if (medication == null) {
            // 복용 기록 엔티티 생성
            medication = Medication.create(user, medicine);
            medicationRepository.save(medication);
        }

        // 복용 기록 추가
        medication.setTakeRecord(LocalDate.now());
        medicationRepository.save(medication);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "복용 확인 등록 성공"),
                HttpStatus.OK);
    }

    // 복약 기록 삭제
    public ResponseEntity<DefaultResponse> deleteMedication(Long userId, DeleteMedicationRequest request) {
        List<String> medicineNames = request.medicineNames();
        User user = userRepository.findByUserId(userId);

        for (String medicineName: medicineNames) {
            Medicine medicine = medicineRepository.findByName(medicineName);
            Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);
            if (medication == null) continue;

            Alarm alarm = alarmRepository.findByMedication(medication);
            if (alarm != null) alarmRepository.delete(alarm);
            medicationRepository.delete(medication);
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "복약 기록 삭제 성공"),
                HttpStatus.OK);
    }

    // 복약 예정 약 조회
    public ResponseEntity<DefaultResponse> getTodayMedication(User user) {
        List<GetTodayMedicationResponse> response = new ArrayList<>();

        List<Medication> medicationList = medicationRepository.findAllByUser(user);
        LocalDate today = LocalDate.now();
        int day = today.getDayOfWeek().getValue();

        for (Medication medication: medicationList) {
            LocalDate startDate = medication.getStartDate();
            LocalDate endDate = medication.getEndDate();
            if (!(startDate == null || endDate == null)) {
                // 오늘이 startDate와 endDate의 사이일 때
                if ((today.isEqual(startDate) || today.isAfter(startDate)) && (today.isEqual(endDate) || today.isBefore(endDate))) {
                    List<Boolean> schedule = medication.getSchedule();

                    // 오늘이 복약 요일이면
                    if (schedule.get(day - 1)) {
                        response.add(new GetTodayMedicationResponse(medication.getMedicine().getName(), medication.getMedicine().getImage(), medication.getAlarm().getTimeList()));
                    }
                }
            }
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "복약 예정 약 조회 성공", response),
                HttpStatus.OK);
    }
}
