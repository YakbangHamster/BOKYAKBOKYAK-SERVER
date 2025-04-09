package com.yakbang.server.controller;

import com.yakbang.server.dto.etc.CustomUserDetails;
import com.yakbang.server.dto.request.ModifyConditionRequest;
import com.yakbang.server.entity.User;
import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.UserConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity addCondition(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> emojiCodeMap) {
        return userConditionService.addCondition(userDetails.getUser(), emojiCodeMap.get("emojiCode"));
    }

    // 컨디션 수정
    @PatchMapping("")
    public ResponseEntity modifyCondition(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ModifyConditionRequest request) {
        return userConditionService.modifyCondition(userDetails.getUser(), request);
    }

    // 컨디션 삭제
    @DeleteMapping("")
    public ResponseEntity deleteCondition(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> dateMap) {
        return userConditionService.deleteCondition(userDetails.getUser().getUserId(), dateMap.get("date"));
    }

    // 등록 컨디션 조회
    @GetMapping("")
    public ResponseEntity getConditions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userConditionService.getConditions(userDetails.getUser());
    }
}
