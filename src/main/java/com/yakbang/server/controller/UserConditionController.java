package com.yakbang.server.controller;

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
}
