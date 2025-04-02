package com.yakbang.server.dto.request;

public record SignInRequest(
        String identity,
        String password
) { }
