package com.yakbang.server.dto.response;

import java.util.List;

public record AlarmResponse(
        String name,
        List<String> timeList,
        Boolean setting
) {}
