package com.yakbang.server.dto.request;

public record SignUpRequest(
        String identity,
        String password,
        String email
) {}
