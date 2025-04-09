package com.yakbang.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakbang.server.config.ChatConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // System 설정
    private static final Map<String, String> SYSTEM_MESSAGE = Map.of(
            "role", "system",
            "content", String.join("\n",
                    "당신은 대한민국에서 승인된 의약품 정보를 기준으로 답변하는 약학 전문가입니다.",
                    "사용자가 언급하는 의약품은 모두 한국에서 판매되는 상품명을 기준으로 해석하세요.",
                    "동일한 상품명이 존재하더라도, 해외에서 쓰이는 제품과 혼동하지 마세요.",
                    "예를 들어, 데스민정은 경구 피임약이 아니라 데스모프레신 성분의 한국 약이며, 리비알정은 파킨슨병 약이 아니라 폐경기 호르몬 대체 요법에 사용하는 티볼론 성분입니다.")
    );

    public String getChatGPT(String message) throws IOException, InterruptedException {
        List<Map<String, String>> messages = List.of(
                SYSTEM_MESSAGE,
                Map.of("role", "user", "content", message)
        );

        // JSON 요청 생성
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4-turbo",
                "messages", messages,
                "temperature", 0.7
        );

        // request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl()))
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readTree(response.body())
                .path("choices").get(0)
                .path("message").path("content")
                .asText();
    }
}
