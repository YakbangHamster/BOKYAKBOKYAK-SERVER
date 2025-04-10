package com.yakbang.server.dto.request;

import java.util.List;

public record MedicineDetailRequest(
        String name,
        List<Boolean> schedule,
        String startDate,
        String endDate,
        Integer number,
        List<String> time
) {}
