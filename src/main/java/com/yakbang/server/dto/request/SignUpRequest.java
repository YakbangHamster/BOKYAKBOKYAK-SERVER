package com.yakbang.server.dto.request;

public record SignUpRequest(
        String identity,
        String password,
        String email,
        String username,
        int age,
        boolean sex,
        double height,
        double weight,
        String disease
) {}
