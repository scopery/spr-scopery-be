package com.company.scopery.modules.aiagent.playground.application.response;

import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.execution.application.response.UsagePolicyWarningInfo;
import com.company.scopery.modules.aiagent.playground.domain.enums.PlaygroundMode;

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
) {

    public static PlaygroundRunResponse from(ExecutionRunResponse r, PlaygroundMode mode) {
        return new PlaygroundRunResponse(
                r.executionLogId(),
                r.requestId(),
                r.status(),
                mode.name(),
                r.eventConfigId(),
                r.eventDefinitionId(),
                r.agentId(),
                r.promptVersionId(),
                r.modelDeploymentId(),
                r.providerCode(),
                r.modelCode(),
                r.modelDeploymentCode(),
                r.outputText(),
                r.inputTokenCount(),
                r.outputTokenCount(),
                r.totalTokenCount(),
                r.estimatedCost(),
                r.latencyMs(),
                r.providerRequestId(),
                r.usagePolicyDecision(),
                r.usagePolicyWarnings(),
                r.errorCode(),
                r.errorMessage());
    }
}
