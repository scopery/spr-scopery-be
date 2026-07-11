package com.company.scopery.modules.aiagent.capability.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateModelParameterCapabilityRequest(
        @NotNull(message = "Model ID is required")
        UUID modelId,

        @NotBlank(message = "Parameter name is required")
        @Pattern(regexp = "^[A-Za-z0-9_]+$",
                 message = "Parameter name must contain only letters, numbers, and underscores")
        String parameterName,

        String apiParameterKey,

        @NotBlank(message = "Support status is required")
        String supportStatus,

        @NotBlank(message = "Value type is required")
        String valueType,

        BigDecimal minValue,
        BigDecimal maxValue,
        String defaultValue,

        @NotNull(message = "nullable is required")
        Boolean nullable,

        String ifNullBehavior,
        String description
) {}
