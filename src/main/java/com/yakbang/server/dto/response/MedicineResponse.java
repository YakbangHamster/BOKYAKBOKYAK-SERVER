package com.yakbang.server.dto.response;

public record MedicineResponse(
        String serial,
        String name,
        String image,
        String efficacy,
        String howToTake
) { }
