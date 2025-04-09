package com.yakbang.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakbang.server.config.MedicineConfig;
import com.yakbang.server.dto.response.MedicineResponse;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.repository.MedicineRepository;
import com.yakbang.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;

    private final MedicineConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // 약 검색 및 추가
    public void addMedicine(String medicineName) throws IOException, InterruptedException {
        // URI 구성
        String uri = String.format("%s?serviceKey=%s&type=%s&item_name=%s",
                config.getBaseUrl(),
                config.getServiceKey(),
                config.getType(),
                URLEncoder.encode(medicineName, StandardCharsets.UTF_8) // 한글 인코딩
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode items = objectMapper.readTree(response.body()).path("body").path("items");

        if (items.isEmpty()) {
            return;
        }

        if (items.isArray()) {
            for (JsonNode item : items) {
                String serial = item.path("ITEM_SEQ").asText();
                String name = item.path("ITEM_NAME").asText();
                String image = item.path("ITEM_IMAGE").asText();

                // 약 등록
                Medicine medicine = Medicine.create(serial, name, image);
                medicineRepository.save(medicine);
            }
        }
    }

    // 약 검색
    public List<MedicineResponse> getMedicine(String medicineName) throws IOException, InterruptedException {
        // 응답 리스트 생성
        List<MedicineResponse> responseList = new ArrayList<>();

        // URI 구성
        String uri = String.format("%s?serviceKey=%s&type=%s&item_name=%s",
                config.getBaseUrl(),
                config.getServiceKey(),
                config.getType(),
                URLEncoder.encode(medicineName, StandardCharsets.UTF_8) // 한글 인코딩
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode items = objectMapper.readTree(response.body()).path("body").path("items");

        if (items.isEmpty()) {
            return responseList;
        }

        if (items.isArray()) {
            for (JsonNode item : items) {
                String serial = item.path("ITEM_SEQ").asText();
                String name = item.path("ITEM_NAME").asText();
                String image = item.path("ITEM_IMAGE").asText();

                responseList.add(new MedicineResponse(serial, name, image));
            }
        }

        return responseList;
    }
}
