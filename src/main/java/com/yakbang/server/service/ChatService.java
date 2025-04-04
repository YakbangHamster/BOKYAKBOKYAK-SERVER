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
        systemMessageJson.put("content", "당신은 의학 전문가입니다.");
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
        conn = (HttpURLConnection) url.openConnection();
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
