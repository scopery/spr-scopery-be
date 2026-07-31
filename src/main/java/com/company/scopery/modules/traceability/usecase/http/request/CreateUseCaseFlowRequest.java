package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateUseCaseFlowRequest(
        @NotBlank String flowType,
        String name,
        UUID sourceStepId,
        String conditionText
) {}
