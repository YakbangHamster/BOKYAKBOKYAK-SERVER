package com.yakbang.server.controller;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.GoogleVisionOCR;
import com.yakbang.server.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> addMedicineWithOCR(@RequestHeader("xAuthToken") String token, @RequestBody Map<String, String> urlMap) throws IOException, ParseException {
        return medicationService.addMedicineWithOCR(urlMap.get("url"));
    }

    // 부작용 조회
    @GetMapping("/side_effects/{serial}")
    public ResponseEntity searchSideEffect(@RequestHeader("xAuthToken") String token, @PathVariable String serial) throws IOException, ParseException {
        return medicationService.searchSideEffect(serial);
    }

    // 복용 확인 등록
    @PostMapping("/record")
    public ResponseEntity addMedication(@RequestHeader("xAuthToken") String token, @RequestBody String serial) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return medicationService.addMedication(userId, serial);
    }
}
