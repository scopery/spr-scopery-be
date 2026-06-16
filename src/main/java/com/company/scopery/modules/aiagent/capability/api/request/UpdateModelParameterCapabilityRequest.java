package com.company.scopery.modules.aiagent.capability.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateModelParameterCapabilityRequest(
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
