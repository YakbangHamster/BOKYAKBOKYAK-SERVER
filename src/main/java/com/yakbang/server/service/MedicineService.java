package com.yakbang.server.service;

import com.yakbang.server.context.StatusCode;
import com.yakbang.server.dto.response.DefaultResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicineService {

    public ResponseEntity<DefaultResponse> searchSideEffect(String serial) throws IOException {
        String jsonResponse = getDrugInfo(serial);
        String sideEffect = drugInfoDataParsing(jsonResponse);

        return new ResponseEntity<>(DefaultResponse.from(StatusCode.OK, "부작용 검색 성공", sideEffect),
                HttpStatus.OK);
    }

    private String getDrugInfo(String serial) throws IOException {
        StringBuilder result = new StringBuilder();
        String urlStr = createDrugInfoURL(serial);

        // URL 객체 생성 및 GET 요청
        URL url = new URL(urlStr);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        // 응답 데이터를 받아올 버퍼 생성
        // try-with-resources (버퍼를 명시적으로 close 하지 않고, 간결하게 처리하기 위함)
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
            String returnLine;

            // 응답 데이터 한 줄씩 기록
            while ((returnLine = br.readLine()) != null) {
                result.append(returnLine + "\n\r");
            }
        } finally {
            urlConnection.disconnect();
        }

        // 응답 데이터 String 형식으로 변환 후 반환
        return result.toString();
    }

    // yaml 파일 읽고 부작용 검색 API url 작성
    private String createDrugInfoURL(String serial) {
        String baseUrl = null;
        String serviceKey = null;
        String type = null;

        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sideEffectConfig.yml");
        if (inputStream != null) {
            Map<String, Object> yamlData = yaml.load(inputStream);
            Map<String, Object> sideEffectApi = (Map<String, Object>) yamlData.get("side_effect_api");

            baseUrl = (String) sideEffectApi.get("base_url");
            serviceKey = (String) sideEffectApi.get("service_key");
            type = (String) sideEffectApi.get("type");
        }

        return String.format("%s?serviceKey=%s&itemSeq=%s&type=%s", baseUrl, serviceKey, serial, type);
    }

    // 부작용 검색 API 데이터 파싱
    private String drugInfoDataParsing(String jsonData) {
        try {
            String seQesitm = null;

            JSONParser jsonParser = new JSONParser();
            // 파싱할 json 문자열
            JSONObject jsonString = (JSONObject) jsonParser.parse(jsonData);

            // item 데이터 받기
            JSONObject body = (JSONObject) jsonString.get("body");
            JSONArray items = (JSONArray) body.get("items");

            // item이 비어있지 않으면 부작용 받아오기
            if (items != null && !items.isEmpty()) {
                JSONObject firstItem = (JSONObject) items.get(0);
                seQesitm = (String) firstItem.get("seQesitm");
            }

            return seQesitm;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
