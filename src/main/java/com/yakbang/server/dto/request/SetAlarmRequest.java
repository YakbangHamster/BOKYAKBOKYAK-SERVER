package com.yakbang.server.dto.request;

public record SetAlarmRequest(
        String medicineName,
        Boolean isSet
) {}
