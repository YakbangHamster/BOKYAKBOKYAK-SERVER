package com.yakbang.server.controller;

import com.yakbang.server.dto.request.AddAlarmRequest;
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
    public ResponseEntity addAlarm(@RequestHeader("xAuthToken") String token, @RequestBody AddAlarmRequest request) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return alarmService.addAlarm(userId, request);
    }

}
