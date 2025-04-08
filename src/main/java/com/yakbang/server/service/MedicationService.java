package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.response.DefaultResponse;
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

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final MedicationRepository medicationRepository;
    private final ChatService chatService;

    // OCR 약 등록
    public ResponseEntity<DefaultResponse> addMedicineWithOCR(String url) throws IOException, ParseException {
        String ocr = GoogleVisionOCR.execute(url);
        String response = chatService.getChatGPT(ocr + "\n이 중에서 약 이름만 골라서 용량이랑 괄호 내용을 빼고 ,(콜론)으로 구분해줘");

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "약 등록 성공", response),
        HttpStatus.OK);
    }

    // 부작용 조회
    public ResponseEntity<DefaultResponse> searchSideEffect(String serial) throws IOException, ParseException {
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
