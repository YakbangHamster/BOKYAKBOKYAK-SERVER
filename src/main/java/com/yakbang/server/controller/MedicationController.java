package com.yakbang.server.controller;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.etc.CustomUserDetails;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.GoogleVisionOCR;
import com.yakbang.server.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/medicines")
@RequiredArgsConstructor
public class MedicationController {
    private final TokenProvider tokenProvider;
    private final MedicationService medicationService;

    // OCR 약 등록
    @PostMapping("")
    public ResponseEntity<?> addMedicineWithOCR(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> urlMap) throws IOException, InterruptedException {
        return medicationService.addMedicineWithOCR(urlMap.get("url"));
    }

    // 부작용 조회
    @GetMapping("/side_effects/{serial}")
    public ResponseEntity searchSideEffect(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String serial) throws IOException, InterruptedException {
        return medicationService.searchSideEffect(serial);
    }

    // 복용 확인 등록
    @PostMapping("/record")
    public ResponseEntity addMedication(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody String serial) {
        return medicationService.addMedication(userDetails.getUser(), serial);
    }
}
