package com.company.scopery.modules.traceability.aimapping.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record GenerateMappingSuggestionsRequest(
        @NotBlank String relationType,
        String scope,
        UUID modelDeploymentId
) {}
