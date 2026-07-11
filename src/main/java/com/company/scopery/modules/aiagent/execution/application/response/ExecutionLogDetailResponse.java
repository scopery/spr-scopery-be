package com.company.scopery.modules.aiagent.execution.application.response;

import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExecutionLogDetailResponse(
        UUID id,
        String requestId,
        UUID eventConfigId,
        String eventConfigCode,
        UUID eventDefinitionId,
        String eventDefinitionCode,
        UUID agentId,
        String agentName,
        UUID promptVersionId,
        Integer promptVersionNumber,
        UUID modelDeploymentId,
        String modelDeploymentCode,
        String triggerSource,
        String status,
        Instant startedAt,
        Instant completedAt,
        Long latencyMs,
        Integer inputTokenCount,
        Integer outputTokenCount,
        Integer totalTokenCount,
        BigDecimal estimatedCost,
        String providerRequestId,
        String errorCode,
        String errorMessage,
        String metadata,
        Instant createdAt,
        Instant updatedAt
) {

    public static ExecutionLogDetailResponse from(ExecutionLog log,
                                                   String eventConfigCode,
                                                   String eventDefinitionCode,
                                                   String agentName,
                                                   Integer promptVersionNumber,
                                                   String modelDeploymentCode) {
        return new ExecutionLogDetailResponse(
                log.id(),
                log.requestId().value(),
                log.eventConfigId(),
                eventConfigCode,
                log.eventDefinitionId(),
                eventDefinitionCode,
                log.agentId(),
                agentName,
                log.promptVersionId(),
                promptVersionNumber,
                log.modelDeploymentId(),
                modelDeploymentCode,
                log.triggerSource().name(),
                log.status().name(),
                log.startedAt(),
                log.completedAt(),
                log.latencyMs(),
                log.inputTokenCount(),
                log.outputTokenCount(),
                log.totalTokenCount(),
                log.estimatedCost(),
                log.providerRequestId(),
                log.errorCode(),
                log.errorMessage(),
                log.metadata(),
                log.createdAt(),
                log.updatedAt()
        );
    }
}