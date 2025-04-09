package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.dto.response.MedicineResponse;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.entity.Medication;
import com.yakbang.server.entity.User;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final MedicationRepository medicationRepository;
    private final ChatService chatService;
    private final MedicineService medicineService;

    // 약 검색
    public ResponseEntity<DefaultResponse> getMedicine(String medicineName) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        List<MedicineResponse> response = medicineService.getSearchMedicine(medicineName);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 검색 성공", response),
                HttpStatus.OK);
    }


    // OCR 약 등록
    public ResponseEntity<DefaultResponse> addMedicineWithOCR(Long userId, String url) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String ocr = GoogleVisionOCR.execute(url);
        String response = chatService.getChatGPT(ocr + "\n이 중에서 약 이름만 골라서 약 이름, 용량, 괄호 내용 적혀있는 부분까지만 포함하고 ;으로만 구분해줘");

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
    public ResponseEntity<DefaultResponse> searchSideEffect(String serial) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        Medicine medicine = medicineRepository.findBySerial(serial);
        String response = medicineService.getCaution(serial);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "부작용 조회 성공", response),
                HttpStatus.OK);
    }

    // 복용 확인 등록
    public ResponseEntity<DefaultResponse> addMedication(User user, String serial) {
        User managedUser = userRepository.findByUserId(user.getUserId());
        Medicine medicine = medicineRepository.findBySerial(serial);

        // 복용 기록 확인하기
        Medication medication = medicationRepository.findByUserAndMedicine(user, medicine);

        // 복용 기록이 없다면
        if (medication == null) {
            // 복용 기록 엔티티 생성
            medication = Medication.create(user, medicine);
            medicationRepository.save(medication);
        }
        
        // 복용 기록 추가
        medication.setTakeRecord(LocalDate.now().toString());

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "복용 확인 등록 성공"),
                HttpStatus.OK);
    }
}
