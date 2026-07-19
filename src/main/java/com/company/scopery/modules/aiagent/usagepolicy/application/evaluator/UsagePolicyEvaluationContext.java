package com.company.scopery.modules.aiagent.usagepolicy.application.evaluator;

import java.time.Instant;
import java.util.UUID;

public record UsagePolicyEvaluationContext(
        UUID eventConfigId,
        UUID eventDefinitionId,
        UUID agentId,
        UUID modelDeploymentId,
        UUID promptVersionId,
        UUID providerId,
        String requestId,
        String triggerSource,
        Instant currentTime,
        String environment,
        Integer estimatedTokensPerRequest
) {
    public UsagePolicyEvaluationContext(
            UUID eventConfigId,
            UUID eventDefinitionId,
            UUID agentId,
            UUID modelDeploymentId,
            UUID promptVersionId,
            UUID providerId,
            String requestId,
            String triggerSource,
            Instant currentTime) {
        this(eventConfigId, eventDefinitionId, agentId, modelDeploymentId, promptVersionId,
                providerId, requestId, triggerSource, currentTime, null, null);
    }
}
