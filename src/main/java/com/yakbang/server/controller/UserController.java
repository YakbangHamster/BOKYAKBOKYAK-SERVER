package com.yakbang.server.controller;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.ImageParsingRequest;
import com.yakbang.server.dto.request.SignInRequest;
import com.yakbang.server.dto.request.SignUpRequest;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.service.GoogleVisionOCR;
import com.yakbang.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    // 로그인
    @PostMapping("/sign-in")
    public ResponseEntity signIn(@RequestBody SignInRequest request) {
        return userService.signIn(request);
    }


    // 테스트용 닉네임 반환 API
    @GetMapping("/username/check")
    public ResponseEntity checkUsername(@RequestParam("username") String username) {
        return userService.checkUsername(username);
    }

    // 테스트용 이미지 분석 API
    @PostMapping("/parse/text/google")
    public ResponseEntity<?> parseImageByGoogleVision(@RequestBody ImageParsingRequest request) throws IOException {
        String parsed = GoogleVisionOCR.execute(request.url());
        return ResponseEntity.ok().body(new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, parsed),
                HttpStatus.OK));
    }
}
