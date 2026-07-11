package com.company.scopery.modules.aiagent.execution.domain.model;

import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionStatus;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.execution.domain.valueobject.ExecutionRequestId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ExecutionLog {

    private final UUID id;
    private final ExecutionRequestId requestId;
    private final UUID eventConfigId;
    private final UUID eventDefinitionId;
    private final UUID agentId;
    private final UUID promptVersionId;
    private final UUID modelDeploymentId;
    private final ExecutionTriggerSource triggerSource;
    private ExecutionStatus status;
    private Instant startedAt;
    private Instant completedAt;
    private Long latencyMs;
    private Integer inputTokenCount;
    private Integer outputTokenCount;
    private Integer totalTokenCount;
    private BigDecimal estimatedCost;
    private String providerRequestId;
    private String errorCode;
    private String errorMessage;
    private String metadata;
    private final Instant createdAt;
    private Instant updatedAt;

    private ExecutionLog(UUID id, ExecutionRequestId requestId,
                         UUID eventConfigId, UUID eventDefinitionId,
                         UUID agentId, UUID promptVersionId, UUID modelDeploymentId,
                         ExecutionTriggerSource triggerSource, ExecutionStatus status,
                         Instant startedAt, Instant completedAt, Long latencyMs,
                         Integer inputTokenCount, Integer outputTokenCount, Integer totalTokenCount,
                         BigDecimal estimatedCost, String providerRequestId,
                         String errorCode, String errorMessage, String metadata,
                         Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.requestId = requestId;
        this.eventConfigId = eventConfigId;
        this.eventDefinitionId = eventDefinitionId;
        this.agentId = agentId;
        this.promptVersionId = promptVersionId;
        this.modelDeploymentId = modelDeploymentId;
        this.triggerSource = triggerSource;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.latencyMs = latencyMs;
        this.inputTokenCount = inputTokenCount;
        this.outputTokenCount = outputTokenCount;
        this.totalTokenCount = totalTokenCount;
        this.estimatedCost = estimatedCost;
        this.providerRequestId = providerRequestId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.metadata = metadata;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ExecutionLog create(ExecutionRequestId requestId,
                                      UUID eventConfigId, UUID eventDefinitionId,
                                      UUID agentId, UUID promptVersionId, UUID modelDeploymentId,
                                      ExecutionTriggerSource triggerSource, String metadata) {
        Instant now = Instant.now();
        return new ExecutionLog(UUID.randomUUID(), requestId, eventConfigId, eventDefinitionId,
                agentId, promptVersionId, modelDeploymentId, triggerSource,
                ExecutionStatus.PENDING, null, null, null,
                null, null, null, null, null, null, null, metadata, now, now);
    }

    public static ExecutionLog reconstitute(UUID id, ExecutionRequestId requestId,
                                            UUID eventConfigId, UUID eventDefinitionId,
                                            UUID agentId, UUID promptVersionId, UUID modelDeploymentId,
                                            ExecutionTriggerSource triggerSource, ExecutionStatus status,
                                            Instant startedAt, Instant completedAt, Long latencyMs,
                                            Integer inputTokenCount, Integer outputTokenCount,
                                            Integer totalTokenCount, BigDecimal estimatedCost,
                                            String providerRequestId, String errorCode, String errorMessage,
                                            String metadata, Instant createdAt, Instant updatedAt) {
        return new ExecutionLog(id, requestId, eventConfigId, eventDefinitionId, agentId,
                promptVersionId, modelDeploymentId, triggerSource, status, startedAt, completedAt,
                latencyMs, inputTokenCount, outputTokenCount, totalTokenCount, estimatedCost,
                providerRequestId, errorCode, errorMessage, metadata, createdAt, updatedAt);
    }

    public void markRunning() {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot transition from terminal status: " + status);
        }
        if (status != ExecutionStatus.PENDING) {
            throw new IllegalStateException("Can only mark RUNNING from PENDING, current: " + status);
        }
        this.status = ExecutionStatus.RUNNING;
        if (this.startedAt == null) {
            this.startedAt = Instant.now();
        }
        this.updatedAt = Instant.now();
    }

    public void markSucceeded(Integer inputTokenCount, Integer outputTokenCount,
                               Integer totalTokenCount, BigDecimal estimatedCost,
                               String providerRequestId, String metadata) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot transition from terminal status: " + status);
        }
        if (status != ExecutionStatus.RUNNING) {
            throw new IllegalStateException("Can only mark SUCCEEDED from RUNNING, current: " + status);
        }
        this.status = ExecutionStatus.SUCCEEDED;
        this.completedAt = Instant.now();
        this.inputTokenCount = inputTokenCount;
        this.outputTokenCount = outputTokenCount;
        this.totalTokenCount = totalTokenCount;
        this.estimatedCost = estimatedCost;
        this.providerRequestId = providerRequestId;
        if (metadata != null) this.metadata = metadata;
        calculateLatency();
        this.updatedAt = Instant.now();
    }

    public void markFailed(String errorCode, String errorMessage,
                            Integer inputTokenCount, Integer outputTokenCount,
                            Integer totalTokenCount, BigDecimal estimatedCost,
                            String providerRequestId, String metadata) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot transition from terminal status: " + status);
        }
        if (status != ExecutionStatus.RUNNING) {
            throw new IllegalStateException("Can only mark FAILED from RUNNING, current: " + status);
        }
        this.status = ExecutionStatus.FAILED;
        this.completedAt = Instant.now();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.inputTokenCount = inputTokenCount;
        this.outputTokenCount = outputTokenCount;
        this.totalTokenCount = totalTokenCount;
        this.estimatedCost = estimatedCost;
        this.providerRequestId = providerRequestId;
        if (metadata != null) this.metadata = metadata;
        calculateLatency();
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot transition from terminal status: " + status);
        }
        if (status != ExecutionStatus.PENDING && status != ExecutionStatus.RUNNING) {
            throw new IllegalStateException("Can only cancel from PENDING or RUNNING, current: " + status);
        }
        this.status = ExecutionStatus.CANCELLED;
        this.completedAt = Instant.now();
        calculateLatency();
        this.updatedAt = Instant.now();
    }

    private void calculateLatency() {
        if (startedAt != null && completedAt != null) {
            this.latencyMs = completedAt.toEpochMilli() - startedAt.toEpochMilli();
        }
    }

    public UUID id() { return id; }
    public ExecutionRequestId requestId() { return requestId; }
    public UUID eventConfigId() { return eventConfigId; }
    public UUID eventDefinitionId() { return eventDefinitionId; }
    public UUID agentId() { return agentId; }
    public UUID promptVersionId() { return promptVersionId; }
    public UUID modelDeploymentId() { return modelDeploymentId; }
    public ExecutionTriggerSource triggerSource() { return triggerSource; }
    public ExecutionStatus status() { return status; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
    public Long latencyMs() { return latencyMs; }
    public Integer inputTokenCount() { return inputTokenCount; }
    public Integer outputTokenCount() { return outputTokenCount; }
    public Integer totalTokenCount() { return totalTokenCount; }
    public BigDecimal estimatedCost() { return estimatedCost; }
    public String providerRequestId() { return providerRequestId; }
    public String errorCode() { return errorCode; }
    public String errorMessage() { return errorMessage; }
    public String metadata() { return metadata; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
