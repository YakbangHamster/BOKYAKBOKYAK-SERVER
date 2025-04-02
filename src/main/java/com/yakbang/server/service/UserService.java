package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.SignInRequest;
import com.yakbang.server.dto.request.SignUpRequest;
import com.yakbang.server.dto.response.CheckUsernameReponse;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.dto.response.SignUpResponse;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.UserRepository;
import com.yakbang.server.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<DefaultResponse> signUp(SignUpRequest request) {
        // 유저 생성 및 비밀번호 인코딩 후 저장
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.create(request.identity(), encodedPassword, request.email(), request.username(), request.age(), request.sex(), request.height(), request.weight(), request.disease());
        userRepository.save(user);

        // 토큰 생성
        Long userId = user.getUserId();
        String accessToken = tokenProvider.createAccessToken(userId);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "회원가입 성공", new SignUpResponse(accessToken)),
                HttpStatus.OK);
    }

    public ResponseEntity<DefaultResponse> signIn(SignInRequest request) {
        // 유저 아이디 확인
        User user = userRepository.findByIdentity(request.identity());

        // 해당하는 아이디가 없거나, 비밀번호가 일치하지 않는 경우
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.BAD_REQUEST, "존재하지 않는 유저입니다."),
                    HttpStatus.NOT_FOUND);
        }

        // 토큰 발급
        Long userId = user.getUserId();
        String accessToken = tokenProvider.createAccessToken(userId);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "로그인 성공", new SignUpResponse(accessToken)),
                HttpStatus.OK);
    }

    public ResponseEntity<DefaultResponse> checkUsername(String username) {
        User user = userRepository.findByUsername(username);
        boolean isExist = false;

        if (user != null) {
            isExist = true;
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "검색 성공", new CheckUsernameReponse(isExist)),
                HttpStatus.OK);
    }

    public String passwordEncoder(String rawPassword) {
        return passwordEncoder.encode(rawPassword); // 비밀번호 해싱
    }
}
