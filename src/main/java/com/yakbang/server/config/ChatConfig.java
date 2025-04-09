package com.yakbang.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chat-api")
@Data
public class ChatConfig {
    private String baseUrl;
    private String apiKey;
}
