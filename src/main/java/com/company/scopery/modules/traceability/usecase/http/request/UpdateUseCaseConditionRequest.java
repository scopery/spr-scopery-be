package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUseCaseConditionRequest(
        @NotBlank String content,
        int displayOrder
) {}
