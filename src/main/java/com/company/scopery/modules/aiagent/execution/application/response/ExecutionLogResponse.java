package com.company.scopery.modules.aiagent.execution.application.response;

import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExecutionLogResponse(
        UUID id,
        String requestId,
        UUID eventConfigId,
        UUID eventDefinitionId,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        String triggerSource,
        String status,
        Instant startedAt,
        Instant completedAt,
        Long latencyMs,
        Integer inputTokenCount,
        Integer outputTokenCount,
        Integer totalTokenCount,
        BigDecimal estimatedCost,
        String errorCode,
        Instant createdAt,
        Instant updatedAt
) {

    public static ExecutionLogResponse from(ExecutionLog log) {
        return new ExecutionLogResponse(
                log.id(),
                log.requestId().value(),
                log.eventConfigId(),
                log.eventDefinitionId(),
                log.agentId(),
                log.promptVersionId(),
                log.modelDeploymentId(),
                log.triggerSource().name(),
                log.status().name(),
                log.startedAt(),
                log.completedAt(),
                log.latencyMs(),
                log.inputTokenCount(),
                log.outputTokenCount(),
                log.totalTokenCount(),
                log.estimatedCost(),
                log.errorCode(),
                log.createdAt(),
                log.updatedAt()
        );
    }
}