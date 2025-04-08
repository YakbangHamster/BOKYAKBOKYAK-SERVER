package com.yakbang.server.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

@Service
public class ChatService {

    public String getChatGPT(String message) throws IOException, ParseException {
        // JSON 요청 생성
        JSONObject requestJson = new JSONObject();
        requestJson.put("model", "gpt-4-turbo");

        JSONArray messages = new JSONArray();

        // System 설정
        JSONObject systemMessageJson = new JSONObject();
        systemMessageJson.put("role", "system");
        systemMessageJson.put("content", "당신은 대한민국에서 승인된 의약품 정보를 기준으로 답변하는 약학 전문가입니다. \n" +
                "사용자가 언급하는 의약품은 모두 한국에서 판매되는 상품명을 기준으로 해석하세요. \n" +
                "동일한 상품명이 존재하더라도, 해외에서 쓰이는 제품과 혼동하지 마세요. \n" +
                "예를 들어, 데스민정은 경구 피임약이 아니라 데스모프레신 성분의 한국 약이며, 리비알정은 파킨슨병 약이 아니라 폐경기 호르몬 대체 요법에 사용하는 티볼론 성분입니다.");
        messages.add(systemMessageJson);

        // User 설정
        JSONObject userMessageJson = new JSONObject();
        userMessageJson.put("role", "user");
        userMessageJson.put("content", message);
        messages.add(userMessageJson);

        requestJson.put("messages", messages);
        requestJson.put("temperature", 0.7);

        // HttpURLConnection 생성
        Map<String, String> configMap = readYaml();

        URL url = new URL(configMap.get("base_url"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + configMap.get("api_key"));
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // 요청 본문 전송
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestJson.toJSONString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 응답 수신
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // 응답 파싱
        return jsonParser(response.toString());
    }

    // yaml 파일 읽가
    private Map<String, String> readYaml() throws IOException {
        Map<String, String> map = new HashMap<>();

        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("chatConfig.yml");
        if (inputStream != null) {
            Map<String, Object> yamlData = yaml.load(inputStream);
            Map<String, Object> chatApi = (Map<String, Object>) yamlData.get("chat_api");

            map.put("base_url", (String) chatApi.get("base_url"));
            map.put("api_key", (String) chatApi.get("api_key"));
        }

        return map;
    }

    // 응답 JSON 파싱
    private String jsonParser(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
        JSONArray choices = (JSONArray) jsonResponse.get("choices");
        JSONObject firstChoice = (JSONObject) choices.get(0);
        JSONObject message = (JSONObject) firstChoice.get("message");

        return (String) message.get("content");
    }
}
