package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record AddUseCaseConditionRequest(
        @NotBlank String conditionType,
        @NotBlank String content,
        int displayOrder
) {}
