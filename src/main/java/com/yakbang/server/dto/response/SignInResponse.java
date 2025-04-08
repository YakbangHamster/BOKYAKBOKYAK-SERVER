package com.yakbang.server.dto.response;

public record SignInResponse(
        String accessToken,
        String refreshToken
) {}
