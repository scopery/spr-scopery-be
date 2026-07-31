package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record AddUseCaseAcceptanceCriterionRequest(
        @NotBlank String title,
        String givenText,
        String whenText,
        String thenText,
        int displayOrder
) {}
