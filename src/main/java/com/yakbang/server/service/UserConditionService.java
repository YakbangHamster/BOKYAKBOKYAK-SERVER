package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.ModifyConditionRequest;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.entity.User;
import com.yakbang.server.entity.UserCondition;
import com.yakbang.server.repository.UserConditionRepository;
import com.yakbang.server.repository.UserRepository;
import com.yakbang.server.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserConditionService {
    private final UserRepository userRepository;
    private final UserConditionRepository userConditionRepository;

    public ResponseEntity<DefaultResponse> addCondition(Long userId, String conditionText) {
        // 유저 받아오기
        User user = userRepository.findByUserId(userId);

        // 컨디션 등록
        UserCondition userCondition = UserCondition.create(user, conditionText, LocalDate.now().toString());
        userConditionRepository.save(userCondition);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "컨디션 등록 성공"),
                HttpStatus.OK);
    }

    public ResponseEntity<DefaultResponse> modifyCondition(Long userId, ModifyConditionRequest request) {
        // 유저, 컨디션 받아오기
        User user = userRepository.findByUserId(userId);
        UserCondition userCondition = userConditionRepository.findByDate(request.date());

        // 컨디션 텍스트 수정
        userCondition.setConditionText(request.conditionText());
        userConditionRepository.save(userCondition);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "컨디션 수정 성공"),
                HttpStatus.OK);
    }
}
