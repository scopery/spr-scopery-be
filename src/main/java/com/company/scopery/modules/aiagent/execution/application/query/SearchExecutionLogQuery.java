package com.company.scopery.modules.aiagent.execution.application.query;

import java.time.Instant;
import java.util.UUID;

public record SearchExecutionLogQuery(
        String requestId,
        UUID eventConfigId,
        UUID eventDefinitionId,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        String triggerSource,
        String status,
        Instant createdFrom,
        Instant createdTo,
        int page,
        int size
) {}