package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record LinkRequirementRequest(
        @NotBlank String requirementId
) {}
