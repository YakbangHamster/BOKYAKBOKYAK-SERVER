package com.yakbang.server.dto.request;

import java.util.List;

public record DeleteMedicationRequest(
        List<String> medicineNames
) {}
