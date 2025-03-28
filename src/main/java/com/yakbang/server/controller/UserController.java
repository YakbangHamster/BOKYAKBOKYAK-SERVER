package com.yakbang.server.controller;

import com.yakbang.server.dto.request.SignUpRequest;
import com.yakbang.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @Operation(summary = "회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity signUp(@Parameter(description = "사용자 회원가입 정보") @RequestBody SignUpRequest request) {
        return userService.signUp(request);
    }

    // 테스트용 닉네임 반환 API
    @GetMapping("/nickname/{user_id}")
    public ResponseEntity nickname(@PathVariable("user_id") String userId) {
        return userService.nickname(userId);
    }

}
