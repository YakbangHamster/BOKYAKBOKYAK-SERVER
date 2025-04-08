package com.yakbang.server.dto.request;

import java.util.List;

public record AddDetailRequest(
        String username,
        int age,
        boolean sex,
        double height,
        double weight,
        List<String> disease
) {}
