package com.yakbang.server.dto.response;

import java.util.List;

public record MyPageResponse(
        String name,
        String email,
        int age,
        boolean sex,
        double height,
        double weight,
        List<String> disease
) {}
