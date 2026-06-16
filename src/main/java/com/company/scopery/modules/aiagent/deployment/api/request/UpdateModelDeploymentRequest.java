package com.company.scopery.modules.aiagent.deployment.api.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record UpdateModelDeploymentRequest(
        @NotBlank(message = "Deployment name is required")
        String name,

        @NotBlank(message = "Provider deployment ID is required")
        String providerDeploymentId,

        String endpointUrl,
        BigDecimal defaultTemperature,
        Integer defaultMaxOutputTokens,
        Boolean isDefault,
        String description
) {}
