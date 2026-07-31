package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record AddUseCaseBusinessRuleRequest(
        @NotBlank String ruleCode,
        @NotBlank String description,
        int displayOrder
) {}
