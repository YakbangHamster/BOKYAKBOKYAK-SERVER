package com.yakbang.server.dto.request;

public record SignUpRequest(
        String userId,
        String password,
        String email,
        String name,
        int age,
        boolean sex,
        double height,
        double weight,
        String disease
) {}
