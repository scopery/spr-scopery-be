package com.company.scopery.modules.aiagent.eventconfig.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateEventConfigRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank String triggerType,
        @NotNull UUID agentId,
        @NotNull UUID promptVersionId,
        @NotNull UUID modelDeploymentId,
        String conditionExpression,
        String description
) {}
