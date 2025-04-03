package com.yakbang.server.controller;

import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/medicines")
@RequiredArgsConstructor
public class MedicationController {
    private final TokenProvider tokenProvider;
    private final MedicationService medicationService;

    // 부작용 조회
    @GetMapping("/side_effects/{serial}")
    public ResponseEntity searchSideEffect(@RequestHeader("xAuthToken") String token, @PathVariable String serial) throws IOException {
        return medicationService.searchSideEffect(serial);
    }

    // 복용 확인 등록
    @PostMapping("/record")
    public ResponseEntity addMedication(@RequestHeader("xAuthToken") String token, @RequestBody String serial) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return medicationService.addMedication(userId, serial);
    }
}
