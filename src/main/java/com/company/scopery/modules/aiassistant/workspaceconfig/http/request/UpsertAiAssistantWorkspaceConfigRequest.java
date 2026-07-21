package com.company.scopery.modules.aiassistant.workspaceconfig.http.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record UpsertAiAssistantWorkspaceConfigRequest(
        UUID modelDeploymentId,
        String modelProvider,
        String modelName,
        @Size(max = 8000) String systemPromptOverride,
        @DecimalMin("0.0") @DecimalMax("2.0") BigDecimal temperatureOverride,
        @Min(1) @Max(32000) Integer maxOutputTokensOverride
) {}
