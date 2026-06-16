package com.company.scopery.modules.aiagent.deployment.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateModelDeploymentRequest(
        @NotNull(message = "Model ID is required")
        UUID modelId,

        @NotBlank(message = "Deployment name is required")
        String name,

        @NotBlank(message = "Deployment code is required")
        String code,

        @NotBlank(message = "Deployment environment is required")
        String environment,

        @NotBlank(message = "Provider deployment ID is required")
        String providerDeploymentId,

        String endpointUrl,
        BigDecimal defaultTemperature,
        Integer defaultMaxOutputTokens,
        Boolean isDefault,
        String description
) {}
