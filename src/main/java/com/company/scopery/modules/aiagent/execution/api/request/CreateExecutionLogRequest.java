package com.company.scopery.modules.aiagent.execution.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateExecutionLogRequest(
        @NotBlank @Size(max = 150) String requestId,
        UUID eventConfigId,
        UUID eventDefinitionId,
        @NotNull UUID agentId,
        @NotNull UUID promptVersionId,
        @NotNull UUID modelDeploymentId,
        @NotBlank String triggerSource,
        String metadata
) {}