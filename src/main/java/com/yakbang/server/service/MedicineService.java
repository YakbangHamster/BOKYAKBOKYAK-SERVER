package com.yakbang.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakbang.server.config.MedicineConfig;
import com.yakbang.server.config.MedicineDetailConfig;
import com.yakbang.server.dto.response.MedicineResponse;
import com.yakbang.server.entity.Medicine;
import com.yakbang.server.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
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
    private final MedicineRepository medicineRepository;

    private final MedicineConfig medicineConfig;
    private final MedicineDetailConfig medicineDetailConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // 약 검색 및 약 추가, serial 반환
    public String addMedicine(String medicineName) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String responseSerial = "";

        JsonNode items = getMedicineItems(medicineName);

        if (items.isEmpty()) {
            return responseSerial;
        }

        if (items.isArray()) {
            for (JsonNode item : items) {
                String serial = item.path("ITEM_SEQ").asText();
                String name = item.path("ITEM_NAME").asText();
                String image = item.path("ITEM_IMAGE").asText();
                List<String> medicineDetail = getMedicineDetail(name);

                Medicine medicine = medicineRepository.findBySerial(serial);
                if (medicine == null) {
                    // 약 등록
                    medicine = Medicine.create(serial, name, image, medicineDetail.get(0), medicineDetail.get(1));
                    medicineRepository.save(medicine);
                }

                responseSerial = serial;
            }
        }

        return responseSerial;
    }

    // 약 검색
    public List<MedicineResponse> getSearchMedicine(String medicineName) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // 응답 리스트 생성
        List<MedicineResponse> responseList = new ArrayList<>();

        JsonNode items = getMedicineItems(medicineName);

        if (items.isEmpty()) {
            return responseList;
        }

        if (items.isArray()) {
            for (JsonNode item : items) {
                String serial = item.path("ITEM_SEQ").asText();
                String name = item.path("ITEM_NAME").asText();
                String image = item.path("ITEM_IMAGE").asText();
                List<String> medicineDetail = getMedicineDetail(name);

                responseList.add(new MedicineResponse(serial, name, image, medicineDetail.get(0), medicineDetail.get(1)));
            }
        }

        return responseList;
    }

    // 의약품제품허가정보 API에서 주의사항 검색
    public String getCaution(String name) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // 응답 리스트 생성
        String responseStr = "";

        // 검색
        JsonNode items = getMedicineDetailItems(name);

        if (items.isEmpty()) {
            return responseStr;
        }

        if (items.isArray()) {
            for (JsonNode item : items) {
                String caution = xmlParser(item.path("NB_DOC_DATA").asText());
                responseStr += " " + caution;
            }
        }

        return responseStr;
    }

    // 의약품제품허가정보 API에서 효능효과, 복용법 검색
    private List<String> getMedicineDetail(String name) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // 응답 리스트 생성
        List<String> responseStr = new ArrayList<>();
        
        // 검색
        JsonNode items = getMedicineDetailItems(name);

        if (items.isEmpty()) {
            return responseStr;
        }

        if (items.isArray()) {
            for (JsonNode item : items) {
                String efficacy = xmlParser(item.path("EE_DOC_DATA").asText());
                String howToTake = xmlParser(item.path("UD_DOC_DATA").asText());

                responseStr.add(efficacy);
                responseStr.add(howToTake);
            }
        }

        return responseStr;
    }

    // 의약품 낱알식별 정보 검색
    private JsonNode getMedicineItems(String medicineName) throws IOException, InterruptedException {
        // URI 구성
        String uri = String.format("%s?serviceKey=%s&type=%s&item_name=%s",
                medicineConfig.getBaseUrl(),
                medicineConfig.getServiceKey(),
                medicineConfig.getType(),
                URLEncoder.encode(medicineName, StandardCharsets.UTF_8) // 한글 인코딩
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body()).path("body").path("items");
    }


    // 의약품제품허가정보 검색
    private JsonNode getMedicineDetailItems(String medicineName) throws IOException, InterruptedException {
        String uri = String.format("%s?serviceKey=%s&type=%s&item_name=%s",
                medicineDetailConfig.getBaseUrl(),
                medicineDetailConfig.getServiceKey(),
                medicineDetailConfig.getType(),
                URLEncoder.encode(medicineName, StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body()).path("body").path("items");
    }


    private String xmlParser(String str) throws IOException, SAXException, ParserConfigurationException {
        String resposne = "";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(str));
        Document doc = builder.parse(is);
        doc.getDocumentElement().normalize();

        NodeList paragraphList = doc.getElementsByTagName("PARAGRAPH");

        for (int i = 0; i < paragraphList.getLength(); i++) {
            Node node = paragraphList.item(i);
            String content = node.getTextContent().trim();

            resposne += " " + content;
        }

        return resposne;
    }
}
