package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateFlowStepRequest(
        @NotBlank String stepType,
        UUID screenContextId,
        String contentJson,
        UUID nextScreenId
) {}
