package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUseCaseBusinessRuleRequest(
        @NotBlank String ruleCode,
        @NotBlank String description,
        int displayOrder
) {}
