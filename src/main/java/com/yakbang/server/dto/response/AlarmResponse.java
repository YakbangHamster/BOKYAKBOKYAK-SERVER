package com.yakbang.server.dto.response;

import java.util.List;

public record AlarmResponse(
        String serial,
        String name,
        String time,
        List<Boolean> schedule,
        String startDate,
        String endDate
) {}
