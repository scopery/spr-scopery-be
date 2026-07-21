package com.company.scopery.modules.aiaction.execution.domain.model;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionExecutionStatus;

import java.time.Instant;
import java.util.UUID;

public class AiActionExecution {

    private final UUID id;
    private final UUID planId;
    private final UUID initiatedByUserId;
    private final String executionKey;
    private AiActionExecutionStatus status;
    private int executionVersion;
    private String workerInstanceId;
    private Instant leaseExpiresAt;
    private Integer currentStepOrdinal;
    private int succeededCount;
    private int failedCount;
    private int skippedCount;
    private int compensatedCount;
    private int cancelledCount;
    private Instant startedAt;
    private Instant completedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiActionExecution(UUID id, UUID planId, UUID initiatedByUserId, String executionKey,
                               AiActionExecutionStatus status, int executionVersion,
                               String workerInstanceId, Instant leaseExpiresAt,
                               Integer currentStepOrdinal, int succeededCount, int failedCount,
                               int skippedCount, int compensatedCount, int cancelledCount,
                               Instant startedAt, Instant completedAt,
                               Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.planId = planId;
        this.initiatedByUserId = initiatedByUserId;
        this.executionKey = executionKey;
        this.status = status;
        this.executionVersion = executionVersion;
        this.workerInstanceId = workerInstanceId;
        this.leaseExpiresAt = leaseExpiresAt;
        this.currentStepOrdinal = currentStepOrdinal;
        this.succeededCount = succeededCount;
        this.failedCount = failedCount;
        this.skippedCount = skippedCount;
        this.compensatedCount = compensatedCount;
        this.cancelledCount = cancelledCount;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiActionExecution create(UUID planId, UUID initiatedByUserId, String executionKey) {
        Instant now = Instant.now();
        return new AiActionExecution(UUID.randomUUID(), planId, initiatedByUserId, executionKey,
                AiActionExecutionStatus.QUEUED, 0, null, null, null,
                0, 0, 0, 0, 0, null, null, now, now);
    }

    public static AiActionExecution reconstitute(UUID id, UUID planId, UUID initiatedByUserId,
                                                  String executionKey, AiActionExecutionStatus status,
                                                  int executionVersion, String workerInstanceId,
                                                  Instant leaseExpiresAt, Integer currentStepOrdinal,
                                                  int succeededCount, int failedCount, int skippedCount,
                                                  int compensatedCount, int cancelledCount,
                                                  Instant startedAt, Instant completedAt,
                                                  Instant createdAt, Instant updatedAt) {
        return new AiActionExecution(id, planId, initiatedByUserId, executionKey, status, executionVersion,
                workerInstanceId, leaseExpiresAt, currentStepOrdinal, succeededCount, failedCount,
                skippedCount, compensatedCount, cancelledCount, startedAt, completedAt, createdAt, updatedAt);
    }

    public void claimLease(String workerId, Instant leaseExpiry) {
        this.workerInstanceId = workerId;
        this.leaseExpiresAt = leaseExpiry;
        this.status = AiActionExecutionStatus.RUNNING;
        this.startedAt = startedAt == null ? Instant.now() : startedAt;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void renewLease(Instant newExpiry) {
        this.leaseExpiresAt = newExpiry;
        this.updatedAt = Instant.now();
    }

    public void releaseLease() {
        this.workerInstanceId = null;
        this.leaseExpiresAt = null;
        this.updatedAt = Instant.now();
    }

    public void recordStepStarted(int stepOrdinal) {
        this.currentStepOrdinal = stepOrdinal;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void recordStepSucceeded() {
        this.succeededCount++;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void recordStepFailed() {
        this.failedCount++;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void recordStepSkipped() {
        this.skippedCount++;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markPausing() {
        this.status = AiActionExecutionStatus.PAUSING;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markPaused() {
        this.status = AiActionExecutionStatus.PAUSED;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markResuming() {
        this.status = AiActionExecutionStatus.RESUMING;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markCancelRequested() {
        this.status = AiActionExecutionStatus.CANCEL_REQUESTED;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markCancelled() {
        this.status = AiActionExecutionStatus.CANCELLED;
        this.completedAt = Instant.now();
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markSucceeded() {
        this.status = AiActionExecutionStatus.SUCCEEDED;
        this.completedAt = Instant.now();
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markPartial() {
        this.status = AiActionExecutionStatus.PARTIAL;
        this.completedAt = Instant.now();
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        this.status = AiActionExecutionStatus.FAILED;
        this.completedAt = Instant.now();
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markCompensating() {
        this.status = AiActionExecutionStatus.COMPENSATING;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public void markCompensated() {
        this.status = AiActionExecutionStatus.COMPENSATED;
        this.executionVersion++;
        this.updatedAt = Instant.now();
    }

    public boolean isLeaseExpired() {
        return leaseExpiresAt != null && Instant.now().isAfter(leaseExpiresAt);
    }

    public boolean isActive() {
        return switch (status) {
            case QUEUED, RUNNING, PAUSING, PAUSED, RESUMING, CANCEL_REQUESTED, COMPENSATING -> true;
            default -> false;
        };
    }

    public UUID id()                          { return id; }
    public UUID planId()                      { return planId; }
    public UUID initiatedByUserId()           { return initiatedByUserId; }
    public String executionKey()              { return executionKey; }
    public AiActionExecutionStatus status()   { return status; }
    public int executionVersion()             { return executionVersion; }
    public String workerInstanceId()          { return workerInstanceId; }
    public Instant leaseExpiresAt()           { return leaseExpiresAt; }
    public Integer currentStepOrdinal()       { return currentStepOrdinal; }
    public int succeededCount()               { return succeededCount; }
    public int failedCount()                  { return failedCount; }
    public int skippedCount()                 { return skippedCount; }
    public int compensatedCount()             { return compensatedCount; }
    public int cancelledCount()               { return cancelledCount; }
    public Instant startedAt()                { return startedAt; }
    public Instant completedAt()              { return completedAt; }
    public Instant createdAt()                { return createdAt; }
    public Instant updatedAt()                { return updatedAt; }
}
