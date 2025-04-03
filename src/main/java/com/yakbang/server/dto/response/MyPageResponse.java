package com.yakbang.server.dto.response;

public record MyPageResponse(
        String name,
        String email,
        int age,
        boolean sex,
        double height,
        double weight,
        String disease
) {}
