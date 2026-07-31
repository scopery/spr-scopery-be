package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUseCaseAcceptanceCriterionRequest(
        @NotBlank String title,
        String givenText,
        String whenText,
        String thenText,
        int displayOrder
) {}
