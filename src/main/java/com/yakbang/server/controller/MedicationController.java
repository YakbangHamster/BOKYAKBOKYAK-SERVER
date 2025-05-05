package com.yakbang.server.controller;

import com.yakbang.server.dto.etc.CustomUserDetails;
import com.yakbang.server.dto.request.AddMedicineRequest;
import com.yakbang.server.dto.request.DeleteMedicationRequest;
import com.yakbang.server.dto.request.MedicineDetailRequest;
import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/medicines")
@RequiredArgsConstructor
public class MedicationController {
    private final TokenProvider tokenProvider;
    private final MedicationService medicationService;

    // 약 검색
    @GetMapping("/search")
    public ResponseEntity getMedicine(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam int page, @RequestBody Map<String, String> medicineNameMap) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        return medicationService.getMedicine(medicineNameMap.get("medicineName"), page);
    }

    // 약 등록
    @PostMapping("")
    public ResponseEntity addMedicine(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AddMedicineRequest request) {
        return medicationService.addMedicine(request);
    }

    // OCR 약 등록
    @PostMapping("/ocr")
    public ResponseEntity<?> addMedicineWithOCR(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> urlMap) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        return medicationService.addMedicineWithOCR(userDetails.getUser().getUserId(), urlMap.get("url"));
    }

    // 부작용 조회
    @GetMapping("/side-effects/{name}")
    public ResponseEntity searchSideEffect(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String name) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        return medicationService.searchSideEffect(name);
    }

    // 등록 약 전체 조회
    @GetMapping("")
    public ResponseEntity getAllMedication(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam int page) {
        return medicationService.getAllMedication(userDetails.getUser(), page);
    }

    // 약 조회
    @GetMapping("/{name}")
    public ResponseEntity getMedication(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String name) {
        return medicationService.getMedication(userDetails.getUser(), name);
    }

    // 레포트 조회
    @GetMapping("/report")
    public ResponseEntity getReport(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return medicationService.getReport(userDetails.getUser());
    }

    // 복용약 상세 등록
    @PostMapping("/detail")
    public ResponseEntity addDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MedicineDetailRequest request) {
        return medicationService.addDetail(userDetails.getUser(), request);
    }

    // 복용약 상세 수정
    @PatchMapping("/detail")
    public ResponseEntity modifyDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MedicineDetailRequest request) {
        return medicationService.modifyDetail(userDetails.getUser(), request);
    }

    // 복용약 상세 조회
    @GetMapping("/detail")
    public ResponseEntity getDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> medicineNameMap) {
        return medicationService.getDetail(userDetails.getUser(), medicineNameMap.get("medicineName"));
    }

    // 복용 확인 등록
    @PostMapping("/record")
    public ResponseEntity addMedication(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> medicineNameMap) {
        return medicationService.addMedication(userDetails.getUser().getUserId(), medicineNameMap.get("medicineName"));
    }

    // 복약 기록 삭제
    @DeleteMapping("record")
    public ResponseEntity deleteMedication(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody DeleteMedicationRequest request) {
        return medicationService.deleteMedication(userDetails.getUser().getUserId(), request);
    }

    // 복약 예정 약 조회
    @GetMapping("/today")
    public ResponseEntity getTodayMedication(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return medicationService.getTodayMedication(userDetails.getUser());
    }
}
