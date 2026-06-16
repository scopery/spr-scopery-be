package com.company.scopery.modules.aiagent.execution.api.request;

import java.time.Instant;
import java.util.UUID;

public record SearchExecutionLogRequest(
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
