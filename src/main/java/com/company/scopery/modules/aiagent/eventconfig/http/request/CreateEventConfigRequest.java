package com.company.scopery.modules.aiagent.eventconfig.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateEventConfigRequest(
        @NotBlank @Size(max = 100) String code,
        @NotBlank @Size(max = 255) String name,
        @NotNull UUID eventDefinitionId,
        String environment,
        @NotBlank String triggerType,
        @NotNull UUID agentId,
        @NotNull UUID promptVersionId,
        @NotNull UUID modelDeploymentId,
        String conditionExpression,
        String description
) {}
