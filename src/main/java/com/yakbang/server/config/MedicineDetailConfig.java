package com.yakbang.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "medicine-detail-api")
@Data
public class MedicineDetailConfig {
    private String baseUrl;
    private String serviceKey;
    private String type;
}
