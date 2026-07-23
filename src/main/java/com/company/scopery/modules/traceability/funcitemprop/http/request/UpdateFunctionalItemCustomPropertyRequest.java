package com.company.scopery.modules.traceability.funcitemprop.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateFunctionalItemCustomPropertyRequest(
        String propValue,
        @NotBlank String fieldType
) {}
