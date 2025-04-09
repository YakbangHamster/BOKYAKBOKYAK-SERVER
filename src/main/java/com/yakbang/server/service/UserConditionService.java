package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.request.ModifyConditionRequest;
import com.yakbang.server.dto.response.DefaultResponse;
import com.yakbang.server.dto.response.UserConditionResponse;
import com.yakbang.server.entity.User;
import com.yakbang.server.entity.UserCondition;
import com.yakbang.server.repository.UserConditionRepository;
import com.yakbang.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConditionService {
    private final UserRepository userRepository;
    private final UserConditionRepository userConditionRepository;

    // 컨디션 등록
    public ResponseEntity<DefaultResponse> addCondition(User user, String emojiCode) {
        // 컨디션 등록
        UserCondition userCondition = UserCondition.create(user, emojiCode, LocalDate.now().toString());
        userConditionRepository.save(userCondition);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "컨디션 등록 성공"),
                HttpStatus.OK);
    }

    // 컨디션 수정
    public ResponseEntity<DefaultResponse> modifyCondition(User user, ModifyConditionRequest request) {
        // 컨디션 받아오기
        UserCondition userCondition = userConditionRepository.findByUserAndDate(user, request.date());

        if (userCondition == null) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.NOT_FOUND, "컨디션을 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND);
        }

        // 컨디션 텍스트 수정
        userCondition.setEmojiCode(request.emojiCode());
        userConditionRepository.save(userCondition);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "컨디션 수정 성공"),
                HttpStatus.OK);
    }

    // 컨디션 삭제
    public ResponseEntity<DefaultResponse> deleteCondition(User user, String date) {
        // 컨디션 받아오기
        UserCondition userCondition = userConditionRepository.findByUserAndDate(user, date);

        if (userCondition == null) {
            return new ResponseEntity<>(DefaultResponse.from(StatusCode.NOT_FOUND, "컨디션을 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND);
        }
        userConditionRepository.delete(userCondition);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "컨디션 삭제 성공"),
                HttpStatus.OK);
    }

    // 등록 컨디션 조회
    public ResponseEntity<DefaultResponse> getConditions(User user) {
        // 전체 컨디션 받아오기
        List<UserCondition> userConditionList = userConditionRepository.findAllByUser(user);

        // 응답으로 보낼 response
        List<UserConditionResponse> userConditionResponse = new ArrayList<>();

        // reponse에 응답값 더하기
        for (int i = 0; i <userConditionList.size(); i++) {
            UserCondition userCondition = userConditionList.get(i);

            userConditionResponse.add(new UserConditionResponse(userCondition.getDate(), userCondition.getEmojiCode()));
        }

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "컨디션 조회 성공", userConditionResponse),
                HttpStatus.OK);
    }
}
