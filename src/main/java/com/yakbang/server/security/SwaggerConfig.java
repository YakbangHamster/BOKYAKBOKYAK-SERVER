package com.yakbang.server.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "Authorization"; // Bearer 토큰 사용

        // JWT 토큰을 HTTP 헤더에서 읽도록 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);
        Components components = new Components().addSecuritySchemes(securitySchemeName, securityScheme);

        return new OpenAPI()
                .components(components) // Security 설정 추가
                .info(apiInfo())
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("복약복약 API") // API의 제목
                //.description("복약복약 api") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}
