package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.SignUpRequest;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ResponseEntity<DefaultResponse> signUp(SignUpRequest request) {
        // 유저 생성 및 저장
        User user = User.create(request.userId(), request.password(), request.email(), request.name(), request.age(), request.sex(), request.height(), request.weight(), request.disease());
        userRepository.save(user);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "회원가입 성공"),
                HttpStatus.OK);
    }

    public ResponseEntity<DefaultResponse> nickname(String userId) {
        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, userRepository.findByUserId(userId).getName()),
                HttpStatus.OK);
    }
}
