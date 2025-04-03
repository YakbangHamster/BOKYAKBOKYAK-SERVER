package com.yakbang.server.dto.request;

import java.util.List;

public record AlarmRequest(
        String serial,
        String time,
        List<Boolean> schedule,
        String startDate,
        String endDate
) {}
