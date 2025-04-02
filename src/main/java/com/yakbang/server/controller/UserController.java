package com.yakbang.server.controller;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.AddDetailRequest;
import com.yakbang.server.dto.request.SignInRequest;
import com.yakbang.server.dto.request.SignUpRequest;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.security.TokenProvider;
import com.yakbang.server.service.GoogleVisionOCR;
import com.yakbang.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final TokenProvider tokenProvider;
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

    // 아이디 확인
    @GetMapping("/identity/check")
    public ResponseEntity checkIdentity(@RequestParam("identity") String identity) {
        return userService.checkIdentity(identity);
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity changePassword(@RequestHeader("xAuthToken") String token, @RequestBody Map<String, String> passwordMap) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return userService.changePassword(userId, passwordMap.get("password"));
    }

    // 상세정보 등록
    @PatchMapping("/detail")
    public ResponseEntity addDetail(@RequestHeader("xAuthToken") String token, @RequestBody AddDetailRequest request) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        return userService.addDetail(userId, request);
    }

    // 테스트용 이미지 분석 API
    @GetMapping("/parse/text/google")
    public ResponseEntity<?> parseImageByGoogleVision(@RequestHeader("xAuthToken") String token, @RequestBody Map<String, String> urlMap) throws IOException {
        String parsed = GoogleVisionOCR.execute(urlMap.get("url"));
        return ResponseEntity.ok().body(new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, parsed),
                HttpStatus.OK));
    }
}
