package com.company.scopery.modules.aiagent.tool.application.response;

import com.company.scopery.modules.aiagent.tool.domain.model.AiToolExecution;

import java.time.Instant;
import java.util.UUID;

public record AiToolExecutionResponse(
        UUID id,
        UUID toolId,
        UUID agentId,
        UUID requestedByUserId,
        String status,
        String approvalState,
        String inputSummary,
        String errorMessage,
        String resultSummary,
        Instant startedAt,
        Instant finishedAt,
        Instant createdAt
) {
    public static AiToolExecutionResponse from(AiToolExecution execution) {
        return new AiToolExecutionResponse(
                execution.id(),
                execution.toolId(),
                execution.agentId(),
                execution.requestedByUserId(),
                execution.status().name(),
                execution.approvalState().name(),
                execution.inputSummary(),
                execution.errorMessage(),
                execution.resultSummary(),
                execution.startedAt(),
                execution.finishedAt(),
                execution.createdAt()
        );
    }
}
