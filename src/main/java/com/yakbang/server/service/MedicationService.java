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
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
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
    public ResponseEntity<DefaultResponse> getMedicine(String medicineName) throws IOException, InterruptedException {
        List<MedicineResponse> response = medicineService.getMedicine(medicineName);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 검색 성공", response),
                HttpStatus.OK);
    }


    // OCR 약 등록
    public ResponseEntity<DefaultResponse> addMedicineWithOCR(String url) throws IOException, InterruptedException {
        String ocr = GoogleVisionOCR.execute(url);
        String response = chatService.getChatGPT(ocr + "\n이 중에서 약 이름만 골라서 약 이름, 용량, 괄호 내용 보이는 부분까지만 포함하고 ;으로 구분해줘");

        String[] medicines = response.split(";");
        for (int i = 0; i < medicines.length; i++) {
            medicineService.addMedicine(medicines[i]);
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 등록 성공"),
        HttpStatus.OK);
    }

    // 부작용 조회
    public ResponseEntity<DefaultResponse> searchSideEffect(String serial) throws IOException, InterruptedException {
        Medicine medicine = medicineRepository.findBySerial(serial);
        String response = chatService.getChatGPT(medicine.getName() + "의 부작용 상세하게 알려줘. 한국 약 기준으로 부작용부터 알려줘.");

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
