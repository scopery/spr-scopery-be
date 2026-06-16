package com.company.scopery.modules.aiagent.playground.application.response;

import com.company.scopery.modules.aiagent.execution.application.response.UsagePolicyWarningInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PlaygroundRunResponse(
        UUID executionLogId,
        String requestId,
        String status,
        String mode,
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
        String usagePolicyDecision,
        List<UsagePolicyWarningInfo> usagePolicyWarnings,
        String errorCode,
        String errorMessage
) {}
