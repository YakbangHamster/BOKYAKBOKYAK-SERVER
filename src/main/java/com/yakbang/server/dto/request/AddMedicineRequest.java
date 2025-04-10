package com.yakbang.server.dto.request;

public record AddMedicineRequest(
        String serial,
        String name,
        String image,
        String efficacy,
        String howToTake
) {}
