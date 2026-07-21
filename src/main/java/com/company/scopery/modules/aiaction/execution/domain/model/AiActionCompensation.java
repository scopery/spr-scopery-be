package com.company.scopery.modules.aiaction.execution.domain.model;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionCompensationStatus;

import java.time.Instant;
import java.util.UUID;

public class AiActionCompensation {

    private final UUID id;
    private final UUID executionId;
    private final UUID stepExecutionId;
    private final UUID requestedByUserId;
    private final String toolCode;
    private AiActionCompensationStatus status;
    private final String comment;
    private final Instant createdAt;
    private Instant completedAt;

    private AiActionCompensation(UUID id, UUID executionId, UUID stepExecutionId,
                                  UUID requestedByUserId, String toolCode,
                                  AiActionCompensationStatus status, String comment,
                                  Instant createdAt, Instant completedAt) {
        this.id = id;
        this.executionId = executionId;
        this.stepExecutionId = stepExecutionId;
        this.requestedByUserId = requestedByUserId;
        this.toolCode = toolCode;
        this.status = status;
        this.comment = comment;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public static AiActionCompensation create(UUID executionId, UUID stepExecutionId,
                                               UUID requestedByUserId, String toolCode, String comment) {
        return new AiActionCompensation(UUID.randomUUID(), executionId, stepExecutionId,
                requestedByUserId, toolCode, AiActionCompensationStatus.PENDING,
                comment, Instant.now(), null);
    }

    public static AiActionCompensation reconstitute(UUID id, UUID executionId, UUID stepExecutionId,
                                                     UUID requestedByUserId, String toolCode,
                                                     AiActionCompensationStatus status, String comment,
                                                     Instant createdAt, Instant completedAt) {
        return new AiActionCompensation(id, executionId, stepExecutionId, requestedByUserId, toolCode,
                status, comment, createdAt, completedAt);
    }

    public void markRunning() {
        this.status = AiActionCompensationStatus.RUNNING;
    }

    public void markCompensated() {
        this.status = AiActionCompensationStatus.COMPENSATED;
        this.completedAt = Instant.now();
    }

    public void markFailed() {
        this.status = AiActionCompensationStatus.FAILED;
        this.completedAt = Instant.now();
    }

    public void markUnsupported() {
        this.status = AiActionCompensationStatus.UNSUPPORTED;
        this.completedAt = Instant.now();
    }

    public UUID id()                             { return id; }
    public UUID executionId()                    { return executionId; }
    public UUID stepExecutionId()                { return stepExecutionId; }
    public UUID requestedByUserId()              { return requestedByUserId; }
    public String toolCode()                     { return toolCode; }
    public AiActionCompensationStatus status()   { return status; }
    public String comment()                      { return comment; }
    public Instant createdAt()                   { return createdAt; }
    public Instant completedAt()                 { return completedAt; }
}
