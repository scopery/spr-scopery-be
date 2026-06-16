package com.company.scopery.modules.aiagent.execution.application.command;

import java.util.UUID;

public record CreateExecutionLogCommand(
        String requestId,
        UUID eventConfigId,
        UUID eventDefinitionId,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        String triggerSource,
        String metadata
) {}