package com.yakbang.server.dto.response;

import java.util.List;

public record AlarmResponse(
        String serial,
        String name,
        List<String> timeList
) {}
