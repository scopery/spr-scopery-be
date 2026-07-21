package com.company.scopery.modules.aiaction.execution.domain.model;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionStepStatus;

import java.time.Instant;
import java.util.UUID;

public class AiActionStepExecution {

    private final UUID id;
    private final UUID executionId;
    private final UUID stepId;
    private final int ordinal;
    private final String toolCode;
    private int attempt;
    private final String idempotencyKey;
    private AiActionStepStatus status;
    private String safeResultSummaryJson;
    private String domainResultRef;
    private String resultVersionToken;
    private String errorCode;
    private Boolean retryable;
    private String auditRef;
    private String outboxRef;
    private final Instant startedAt;
    private Instant completedAt;

    private AiActionStepExecution(UUID id, UUID executionId, UUID stepId, int ordinal, String toolCode,
                                   int attempt, String idempotencyKey, AiActionStepStatus status,
                                   String safeResultSummaryJson, String domainResultRef,
                                   String resultVersionToken, String errorCode, Boolean retryable,
                                   String auditRef, String outboxRef,
                                   Instant startedAt, Instant completedAt) {
        this.id = id;
        this.executionId = executionId;
        this.stepId = stepId;
        this.ordinal = ordinal;
        this.toolCode = toolCode;
        this.attempt = attempt;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.safeResultSummaryJson = safeResultSummaryJson;
        this.domainResultRef = domainResultRef;
        this.resultVersionToken = resultVersionToken;
        this.errorCode = errorCode;
        this.retryable = retryable;
        this.auditRef = auditRef;
        this.outboxRef = outboxRef;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public static AiActionStepExecution create(UUID executionId, UUID stepId, int ordinal,
                                                String toolCode, int attempt, String idempotencyKey) {
        return new AiActionStepExecution(UUID.randomUUID(), executionId, stepId, ordinal, toolCode,
                attempt, idempotencyKey, AiActionStepStatus.RUNNING,
                null, null, null, null, null, null, null,
                Instant.now(), null);
    }

    public static AiActionStepExecution reconstitute(UUID id, UUID executionId, UUID stepId, int ordinal,
                                                      String toolCode, int attempt, String idempotencyKey,
                                                      AiActionStepStatus status, String safeResultSummaryJson,
                                                      String domainResultRef, String resultVersionToken,
                                                      String errorCode, Boolean retryable, String auditRef,
                                                      String outboxRef, Instant startedAt, Instant completedAt) {
        return new AiActionStepExecution(id, executionId, stepId, ordinal, toolCode, attempt, idempotencyKey,
                status, safeResultSummaryJson, domainResultRef, resultVersionToken, errorCode, retryable,
                auditRef, outboxRef, startedAt, completedAt);
    }

    public void markSucceeded(String safeResultSummaryJson, String domainResultRef,
                               String resultVersionToken, String auditRef, String outboxRef) {
        this.status = AiActionStepStatus.SUCCEEDED;
        this.safeResultSummaryJson = safeResultSummaryJson;
        this.domainResultRef = domainResultRef;
        this.resultVersionToken = resultVersionToken;
        this.auditRef = auditRef;
        this.outboxRef = outboxRef;
        this.completedAt = Instant.now();
    }

    public void markFailed(String errorCode, boolean retryable) {
        this.status = AiActionStepStatus.FAILED;
        this.errorCode = errorCode;
        this.retryable = retryable;
        this.completedAt = Instant.now();
    }

    public void markSkipped() {
        this.status = AiActionStepStatus.SKIPPED;
        this.completedAt = Instant.now();
    }

    public void markCompensated() {
        this.status = AiActionStepStatus.COMPENSATED;
        this.completedAt = Instant.now();
    }

    public UUID id()                         { return id; }
    public UUID executionId()                { return executionId; }
    public UUID stepId()                     { return stepId; }
    public int ordinal()                     { return ordinal; }
    public String toolCode()                 { return toolCode; }
    public int attempt()                     { return attempt; }
    public String idempotencyKey()           { return idempotencyKey; }
    public AiActionStepStatus status()       { return status; }
    public String safeResultSummaryJson()    { return safeResultSummaryJson; }
    public String domainResultRef()          { return domainResultRef; }
    public String resultVersionToken()       { return resultVersionToken; }
    public String errorCode()                { return errorCode; }
    public Boolean retryable()               { return retryable; }
    public String auditRef()                 { return auditRef; }
    public String outboxRef()                { return outboxRef; }
    public Instant startedAt()               { return startedAt; }
    public Instant completedAt()             { return completedAt; }
}
