package com.yakbang.server.controller;

import com.yakbang.server.dto.request.AlarmRequest;
import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {
    private final TokenProvider tokenProvider;
    private final AlarmService alarmService;

    // 알림 등록
    @PostMapping("")
    public ResponseEntity addAlarm(@RequestHeader("xAuthToken") String token, @RequestBody AlarmRequest request) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return alarmService.addAlarm(userId, request);
    }

    // 알림 수정
    @PatchMapping("")
    public ResponseEntity modifyAlarm(@RequestHeader("xAuthToken") String token, @RequestBody AlarmRequest request) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return alarmService.modifyAlarm(userId, request);
    }

    // 등록 알림 조회
    @GetMapping("")
    public ResponseEntity getAlarm(@RequestHeader("xAuthToken") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return alarmService.getAlarm(userId);
    }
}
