package com.yakbang.server.dto.response;

import java.util.List;

public record GetTodayMedicationResponse(
        String medicineName,
        String image,
        List<String> timeList
) {}
