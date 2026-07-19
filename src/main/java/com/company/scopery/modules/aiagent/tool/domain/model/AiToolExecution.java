package com.company.scopery.modules.aiagent.tool.domain.model;

import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolApprovalState;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolExecutionStatus;

import java.time.Instant;
import java.util.UUID;

public class AiToolExecution {

    private final UUID id;
    private final UUID toolId;
    private final UUID agentId;
    private final UUID requestedByUserId;
    private AiToolExecutionStatus status;
    private AiToolApprovalState approvalState;
    private String inputSummary;
    private String errorMessage;
    private String resultSummary;
    private Instant startedAt;
    private Instant finishedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiToolExecution(UUID id, UUID toolId, UUID agentId, UUID requestedByUserId,
                            AiToolExecutionStatus status, AiToolApprovalState approvalState,
                            String inputSummary, String errorMessage, String resultSummary,
                            Instant startedAt, Instant finishedAt,
                            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.toolId = toolId;
        this.agentId = agentId;
        this.requestedByUserId = requestedByUserId;
        this.status = status;
        this.approvalState = approvalState;
        this.inputSummary = inputSummary;
        this.errorMessage = errorMessage;
        this.resultSummary = resultSummary;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiToolExecution start(UUID toolId, UUID agentId, UUID requestedByUserId,
                                        String inputSummary, AiToolApprovalState approvalState) {
        if (toolId == null) {
            throw new IllegalArgumentException("toolId is required");
        }
        Instant now = Instant.now();
        return new AiToolExecution(UUID.randomUUID(), toolId, agentId, requestedByUserId,
                AiToolExecutionStatus.RUNNING, approvalState != null ? approvalState : AiToolApprovalState.NOT_REQUIRED,
                truncate(inputSummary, 1000), null, null, now, null, now, now);
    }

    public static AiToolExecution reconstitute(UUID id, UUID toolId, UUID agentId, UUID requestedByUserId,
                                               AiToolExecutionStatus status, AiToolApprovalState approvalState,
                                               String inputSummary, String errorMessage, String resultSummary,
                                               Instant startedAt, Instant finishedAt,
                                               Instant createdAt, Instant updatedAt) {
        return new AiToolExecution(id, toolId, agentId, requestedByUserId, status, approvalState,
                inputSummary, errorMessage, resultSummary, startedAt, finishedAt, createdAt, updatedAt);
    }

    public void markNoOp(String resultSummary) {
        this.status = AiToolExecutionStatus.NO_OP;
        this.resultSummary = truncate(resultSummary, 2000);
        this.finishedAt = Instant.now();
        this.updatedAt = this.finishedAt;
    }

    public void markSucceeded(String resultSummary) {
        this.status = AiToolExecutionStatus.SUCCEEDED;
        this.resultSummary = truncate(resultSummary, 2000);
        this.finishedAt = Instant.now();
        this.updatedAt = this.finishedAt;
    }

    public void markFailed(String errorMessage) {
        this.status = AiToolExecutionStatus.FAILED;
        this.errorMessage = truncate(errorMessage, 2000);
        this.finishedAt = Instant.now();
        this.updatedAt = this.finishedAt;
    }

    public void markDenied(String reason) {
        this.status = AiToolExecutionStatus.DENIED;
        this.errorMessage = truncate(reason, 2000);
        this.finishedAt = Instant.now();
        this.updatedAt = this.finishedAt;
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }

    public UUID id() { return id; }
    public UUID toolId() { return toolId; }
    public UUID agentId() { return agentId; }
    public UUID requestedByUserId() { return requestedByUserId; }
    public AiToolExecutionStatus status() { return status; }
    public AiToolApprovalState approvalState() { return approvalState; }
    public String inputSummary() { return inputSummary; }
    public String errorMessage() { return errorMessage; }
    public String resultSummary() { return resultSummary; }
    public Instant startedAt() { return startedAt; }
    public Instant finishedAt() { return finishedAt; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
