package com.yakbang.server.dto.request;

import java.util.List;

public record AlarmRequest(
        String serial,
        List<String> timeList
) {}
