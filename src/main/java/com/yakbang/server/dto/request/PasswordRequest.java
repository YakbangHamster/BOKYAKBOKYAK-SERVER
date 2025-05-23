package com.yakbang.server.dto.request;

public record PasswordRequest(
        String identity,
        String password
) {}
