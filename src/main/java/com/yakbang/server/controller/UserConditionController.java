package com.yakbang.server.controller;

import com.yakbang.server.dto.request.ModifyConditionRequest;
import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.UserConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/conditions")
@RequiredArgsConstructor
public class UserConditionController {
    private final TokenProvider tokenProvider;
    private final UserConditionService userConditionService;

    // 컨디션 등록
    @PostMapping("")
    public ResponseEntity addCondition(@RequestHeader("xAuthToken") String token, @RequestBody Map<String, String> conditionTextMap) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return userConditionService.addCondition(userId, conditionTextMap.get("conditionText"));
    }

    // 컨디션 수정
    @PatchMapping("")
    public ResponseEntity modifyCondition(@RequestHeader("xAuthToken") String token, @RequestBody ModifyConditionRequest request) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return userConditionService.modifyCondition(userId, request);
    }

    // 등록 컨디션 조회
    @GetMapping("")
    public ResponseEntity getConditions(@RequestHeader("xAuthToken") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return userConditionService.getConditions(userId);
    }
}
