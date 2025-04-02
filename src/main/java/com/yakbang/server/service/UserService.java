package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public ResponseEntity<DefaultResponse> signUp(SignUpRequest request) {
        // 유저 생성 및 저장
        User user = User.create(request.identity(), request.password(), request.email(), request.username(), request.age(), request.sex(), request.height(), request.weight(), request.disease());
        userRepository.save(user);

        // 토큰 생성
        Long userId = user.getUserId();
        String accessToken = tokenProvider.createAccessToken(userId);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "회원가입 성공", new SignUpResponse(accessToken)),
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
}
