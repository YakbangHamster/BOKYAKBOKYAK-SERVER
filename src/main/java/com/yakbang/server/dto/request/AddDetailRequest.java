package com.yakbang.server.dto.request;

public record AddDetailRequest(
        String username,
        int age,
        boolean sex,
        double height,
        double weight,
        String disease
) {}
