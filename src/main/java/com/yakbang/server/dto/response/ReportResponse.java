package com.yakbang.server.dto.response;

import java.util.List;

public record ReportResponse(
        String username,
        String startDate,
        String endDate,
        List<Integer> schedule,
        List<ReportMedication> medication,
        double compliance
) {}
