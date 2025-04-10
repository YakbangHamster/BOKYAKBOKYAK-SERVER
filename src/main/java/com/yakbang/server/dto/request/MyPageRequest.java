package com.yakbang.server.dto.request;

import java.util.List;

public record MyPageRequest(
        String name,
        String email,
        Integer age,
        Boolean sex,
        Double height,
        Double weight,
        List<String> disease
) {}
