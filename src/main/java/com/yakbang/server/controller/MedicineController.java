package com.yakbang.server.controller;

import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/medicines")
@RequiredArgsConstructor
public class MedicineController {
    private final TokenProvider tokenProvider;
    private final MedicineService medicineService;

    // 부작용 확인
    @GetMapping("/side_effects/{serial}")
    public ResponseEntity searchSideEffect(@RequestHeader("xAuthToken") String token, @PathVariable String serial) throws IOException {
        return medicineService.searchSideEffect(serial);
    }

}
