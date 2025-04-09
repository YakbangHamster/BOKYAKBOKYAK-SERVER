package com.yakbang.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "medicine-api")
@Data
public class MedicineConfig {
    private String baseUrl;
    private String serviceKey;
    private String type;
}
