package com.yakbang.server.dto.request;

public record SignUpRequest(
        String userId,
        String password,
        String name
) {}
