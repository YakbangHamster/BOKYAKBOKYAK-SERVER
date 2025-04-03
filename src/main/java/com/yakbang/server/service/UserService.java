package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.SignInRequest;
import com.yakbang.server.dto.request.SignUpRequest;
import com.yakbang.server.dto.request.AddDetailRequest;
import com.yakbang.server.dto.response.CheckUsernameReponse;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.dto.response.MyPageResponse;
import com.yakbang.server.dto.response.SignUpResponse;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.UserRepository;
import com.yakbang.server.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public ResponseEntity<DefaultResponse> signUp(SignUpRequest request) {
        // 유저 생성 및 비밀번호 인코딩 후 저장
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.create(request.identity(), encodedPassword, request.email());
        userRepository.save(user);

        // 토큰 생성
        Long userId = user.getUserId();
        String accessToken = tokenProvider.createAccessToken(userId);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "회원가입 성공", new SignUpResponse(accessToken)),
                HttpStatus.OK);
    }

    // 로그인
    public ResponseEntity<DefaultResponse> signIn(SignInRequest request) {
        // 유저 아이디 확인
        User user = userRepository.findByIdentity(request.identity());

        // 해당하는 아이디가 없거나, 비밀번호가 일치하지 않는 경우
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.BAD_REQUEST, "존재하지 않는 유저 또는 잘못된 비밀번호입니다."),
                    HttpStatus.NOT_FOUND);
        }

        // 토큰 발급
        Long userId = user.getUserId();
        String accessToken = tokenProvider.createAccessToken(userId);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "로그인 성공", new SignUpResponse(accessToken)),
                HttpStatus.OK);
    }

    // 아이디 확인
    public ResponseEntity<DefaultResponse> checkIdentity(String identity) {
        // 아이디로 유저 검색
        User user = userRepository.findByIdentity(identity);
        boolean isExist = false;

        // 유저가 있으면 true
        if (user != null) {
            isExist = true;
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "검색 성공", new CheckUsernameReponse(isExist)),
                HttpStatus.OK);
    }

    // 비밀번호 변경
    public ResponseEntity<DefaultResponse> changePassword(Long userId, String password) {
        // 사용자 받아오기
        User user = userRepository.findByUserId(userId);

        // 비밀번호 인코딩해서 변경하기
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "비밀번호 변경 성공"),
                HttpStatus.OK);
    }

    // 상세정보 등록
    public ResponseEntity<DefaultResponse> addDetail(Long userId, AddDetailRequest request) {
        // 사용자 받아오기
        User user = userRepository.findByUserId(userId);
        
        // 상세정보 등록하기
        user.setUsername(request.username());
        user.setAge(request.age());
        user.setSex(request.sex());
        user.setHeight(request.height());
        user.setWeight(request.weight());
        user.setDisease(request.disease());
        userRepository.save(user);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "상세정보 등록 성공"),
                HttpStatus.OK);
    }

    // 마이페이지 조회
    public ResponseEntity<DefaultResponse> getMyPage(Long userId) {
        User user = userRepository.findByUserId(userId);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "마이페이지 조회 성공", new MyPageResponse(user.getUsername(), user.getEmail(), user.getAge(), user.isSex(), user.getHeight(), user.getWeight(), user.getDisease())),
                HttpStatus.OK);
    }

    public String passwordEncoder(String rawPassword) {
        return passwordEncoder.encode(rawPassword); // 비밀번호 해싱
    }
}
