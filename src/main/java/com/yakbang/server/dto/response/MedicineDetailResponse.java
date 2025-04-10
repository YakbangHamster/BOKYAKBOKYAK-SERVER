package com.yakbang.server.dto.response;

import java.util.List;

public record MedicineDetailResponse(
        String name,
        List<Boolean> schedule,
        String startDate,
        String endDate,
        Integer number,
        List<String> time
) {}
