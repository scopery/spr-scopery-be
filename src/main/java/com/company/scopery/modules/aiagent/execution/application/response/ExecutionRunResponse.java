package com.company.scopery.modules.aiagent.execution.application.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExecutionRunResponse(
        UUID executionLogId,
        String requestId,
        String status,
        UUID eventConfigId,
        UUID eventDefinitionId,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        String providerCode,
        String modelCode,
        String modelDeploymentCode,
        String outputText,
        Integer inputTokenCount,
        Integer outputTokenCount,
        Integer totalTokenCount,
        BigDecimal estimatedCost,
        Long latencyMs,
        String providerRequestId,
        String errorCode,
        String errorMessage,
        String usagePolicyDecision,
        List<UsagePolicyWarningInfo> usagePolicyWarnings
) {}
