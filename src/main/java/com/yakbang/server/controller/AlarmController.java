package com.yakbang.server.controller;

import com.yakbang.server.dto.etc.CustomUserDetails;
import com.yakbang.server.dto.request.AlarmRequest;
import com.yakbang.server.dto.request.SetAlarmRequest;
import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {
    private final TokenProvider tokenProvider;
    private final AlarmService alarmService;

    // 알림 등록
    @PostMapping("")
    public ResponseEntity addAlarm(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AlarmRequest request) {
        return alarmService.addAlarm(userDetails.getUser(), request);
    }

    // 알림 수정
    @PatchMapping("")
    public ResponseEntity modifyAlarm(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AlarmRequest request) {
        return alarmService.modifyAlarm(userDetails.getUser(), request);
    }

    // 알림 삭제
    @DeleteMapping("")
    public ResponseEntity deleteAlarm(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> serialMap) {
        return alarmService.deleteAlarm(userDetails.getUser(), serialMap.get("serial"));
    }

    // 알림 상태 변화
    @PatchMapping("/set")
    public ResponseEntity setAlarm(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody SetAlarmRequest request) {
        return alarmService.setAlarm(userDetails.getUser(), request);
    }

    // 등록 알림 조회
    @GetMapping("")
    public ResponseEntity getAlarm(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return alarmService.getAlarm(userDetails.getUser());
    }
}
