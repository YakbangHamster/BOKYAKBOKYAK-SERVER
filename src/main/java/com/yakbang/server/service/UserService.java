package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.SignInRequest;
import com.yakbang.server.dto.request.SignUpRequest;
import com.yakbang.server.dto.request.AddDetailRequest;
import com.yakbang.server.dto.response.CheckUsernameReponse;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.dto.response.MyPageResponse;
import com.yakbang.server.dto.response.SignInResponse;
import com.yakbang.server.entity.RefreshToken;
import com.yakbang.server.entity.User;
import com.yakbang.server.repository.RefreshTokenRepository;
import com.yakbang.server.repository.UserRepository;
import com.yakbang.server.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public ResponseEntity<DefaultResponse> signUp(SignUpRequest request) {
        if (userRepository.existsByIdentity(request.identity())) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.CONFLICT, "이미 존재하는 회원입니다."),
                    HttpStatus.CONFLICT);
        }

        // 유저 생성 및 비밀번호 인코딩 후 저장
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.create(request.identity(), encodedPassword, request.email());
        userRepository.save(user);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "회원가입 성공"),
                HttpStatus.OK);
    }

    // 로그인
    @Transactional
    public ResponseEntity<DefaultResponse> signIn(SignInRequest request) {
        // 유저 아이디 확인
        User user = userRepository.findByIdentity(request.identity());

        // 해당하는 아이디가 없거나, 비밀번호가 일치하지 않는 경우
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.BAD_REQUEST, "존재하지 않는 유저 또는 잘못된 비밀번호입니다."),
                    HttpStatus.NOT_FOUND);
        }

        // 토큰 생성
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);
        LocalDateTime refreshExpiration = tokenProvider.getRefreshTokenExpiryTime();

        // 기존 리프레시 토큰 있는지 확인
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        existing -> existing.updateToken(refreshToken, refreshExpiration),
                        () -> {
                            RefreshToken newRefreshToken = RefreshToken.create(
                                    refreshToken,
                                    refreshExpiration,
                                    user
                            );
                            refreshTokenRepository.save(newRefreshToken);
                        }
                );

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "로그인 성공", new SignInResponse(accessToken, refreshToken)),
                HttpStatus.OK);
    }

    public ResponseEntity<DefaultResponse> reissue(String refreshToken) {
        // 1. refresh token 유효성 검증
        if (!tokenProvider.isValidToken(refreshToken)) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다."),
                    HttpStatus.UNAUTHORIZED);
        }

        // 2. refresh token에서 identity 추출
        String identity = tokenProvider.getIdentityFromToken(refreshToken);

        // 3. DB에서 refresh token 일치 확인
        User user = userRepository.findByIdentity(identity);
        if (user == null) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.NOT_FOUND, "사용자를 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND);
        }

        Optional<RefreshToken> saved = refreshTokenRepository.findByUser(user);
        if(saved.isEmpty()) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.UNAUTHORIZED, "Refresh Token을 찾을 수 없습니다."),
                    HttpStatus.UNAUTHORIZED);
        } else if (!saved.get().getToken().equals(refreshToken)) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.UNAUTHORIZED, "Refresh Token이 일치하지 않습니다."),
                    HttpStatus.UNAUTHORIZED);
        }

        // 4. access token만 재발급
        String newAccessToken = tokenProvider.generateAccessToken(user);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "로그인 성공", new SignInResponse(newAccessToken, refreshToken)),
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
    public ResponseEntity<DefaultResponse> changePassword(User user, String password) {
        // 비밀번호 인코딩해서 변경하기
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "비밀번호 변경 성공"),
                HttpStatus.OK);
    }

    // 상세정보 등록
    public ResponseEntity<DefaultResponse> addDetail(User user, AddDetailRequest request) {
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
    public ResponseEntity<DefaultResponse> getMyPage(User user) {
        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "마이페이지 조회 성공", new MyPageResponse(user.getUsername(), user.getEmail(), user.getAge(), user.isSex(), user.getHeight(), user.getWeight(), user.getDisease())),
                HttpStatus.OK);
    }

    public String passwordEncoder(String rawPassword) {
        return passwordEncoder.encode(rawPassword); // 비밀번호 해싱
    }
}
