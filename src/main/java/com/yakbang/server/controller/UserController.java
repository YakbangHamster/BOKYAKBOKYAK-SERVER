package com.yakbang.server.controller;

import com.yakbang.server.dto.etc.CustomUserDetails;
import com.yakbang.server.dto.request.AddDetailRequest;
import com.yakbang.server.dto.request.MyPageRequest;
import com.yakbang.server.dto.request.SignInRequest;
import com.yakbang.server.dto.request.SignUpRequest;
import com.yakbang.server.service.ChatService;
import com.yakbang.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ChatService chatService;

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

    // Access Token 재발급
    @PostMapping("/reissue")
    public ResponseEntity reissue(@RequestHeader("refresh") String refreshToken) {
        return userService.reissue(refreshToken.replace("Bearer ", ""));
    }

    // 아이디 확인
    @GetMapping("/identity/check")
    public ResponseEntity checkIdentity(@RequestParam("identity") String identity) {
        return userService.checkIdentity(identity);
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity changePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> passwordMap) {
        return userService.changePassword(userDetails.getUser(), passwordMap.get("password"));
    }

    // 상세정보 등록
    @PatchMapping("/detail")
    public ResponseEntity addDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AddDetailRequest request) {
        return userService.addDetail(userDetails.getUser(), request);
    }

    // 마이페이지 수정
    @PatchMapping("/myPage")
    public ResponseEntity modifyMyPage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MyPageRequest request) {
        return userService.modifyMyPage(userDetails.getUser().getUserId(), request);
    }

    // 마이페이지 조회
    @GetMapping("/myPage")
    public ResponseEntity getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.getMyPage(userDetails.getUser());
    }
}