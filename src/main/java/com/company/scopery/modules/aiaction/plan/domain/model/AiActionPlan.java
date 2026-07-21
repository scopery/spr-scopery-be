package com.company.scopery.modules.aiaction.plan.domain.model;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionPlanStatus;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;

import java.time.Instant;
import java.util.UUID;

public class AiActionPlan {

    private final UUID id;
    private final UUID requestId;
    private final int planNumber;
    private AiActionPlanStatus status;
    private String policyCode;
    private int policyVersion;
    private String planHash;
    private String contextHash;
    private String sourceStateHash;
    private AiActionRiskLevel riskLevel;
    private AiActionExecutionMode executionMode;
    private boolean requiresConfirmation;
    private int stepCount;
    private int targetCount;
    private String summary;
    private int version;
    private Instant expiresAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiActionPlan(UUID id, UUID requestId, int planNumber, AiActionPlanStatus status,
                          String policyCode, int policyVersion, String planHash,
                          String contextHash, String sourceStateHash,
                          AiActionRiskLevel riskLevel, AiActionExecutionMode executionMode,
                          boolean requiresConfirmation, int stepCount, int targetCount,
                          String summary, int version, Instant expiresAt,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.requestId = requestId;
        this.planNumber = planNumber;
        this.status = status;
        this.policyCode = policyCode;
        this.policyVersion = policyVersion;
        this.planHash = planHash;
        this.contextHash = contextHash;
        this.sourceStateHash = sourceStateHash;
        this.riskLevel = riskLevel;
        this.executionMode = executionMode;
        this.requiresConfirmation = requiresConfirmation;
        this.stepCount = stepCount;
        this.targetCount = targetCount;
        this.summary = summary;
        this.version = version;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiActionPlan create(UUID requestId, int planNumber, String policyCode, int policyVersion) {
        Instant now = Instant.now();
        return new AiActionPlan(UUID.randomUUID(), requestId, planNumber, AiActionPlanStatus.DRAFT,
                policyCode, policyVersion, null, null, null,
                null, null, false, 0, 0, null, 1, null, now, now);
    }

    public static AiActionPlan reconstitute(UUID id, UUID requestId, int planNumber,
                                             AiActionPlanStatus status, String policyCode, int policyVersion,
                                             String planHash, String contextHash, String sourceStateHash,
                                             AiActionRiskLevel riskLevel, AiActionExecutionMode executionMode,
                                             boolean requiresConfirmation, int stepCount, int targetCount,
                                             String summary, int version, Instant expiresAt,
                                             Instant createdAt, Instant updatedAt) {
        return new AiActionPlan(id, requestId, planNumber, status, policyCode, policyVersion,
                planHash, contextHash, sourceStateHash, riskLevel, executionMode,
                requiresConfirmation, stepCount, targetCount, summary, version, expiresAt,
                createdAt, updatedAt);
    }

    public void markValidating() {
        this.status = AiActionPlanStatus.VALIDATING;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markInvalid() {
        this.status = AiActionPlanStatus.INVALID;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markPreviewReady(String planHash, String contextHash, String sourceStateHash,
                                  AiActionRiskLevel riskLevel, AiActionExecutionMode executionMode,
                                  boolean requiresConfirmation, int stepCount, int targetCount,
                                  String summary, Instant expiresAt) {
        this.planHash = planHash;
        this.contextHash = contextHash;
        this.sourceStateHash = sourceStateHash;
        this.riskLevel = riskLevel;
        this.executionMode = executionMode;
        this.requiresConfirmation = requiresConfirmation;
        this.stepCount = stepCount;
        this.targetCount = targetCount;
        this.summary = summary;
        this.expiresAt = expiresAt;
        this.status = requiresConfirmation ? AiActionPlanStatus.WAITING_CONFIRMATION : AiActionPlanStatus.PREVIEW_READY;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markConfirmed() {
        this.status = AiActionPlanStatus.CONFIRMED;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markExecutionQueued() {
        this.status = AiActionPlanStatus.EXECUTION_QUEUED;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markExecuting() {
        this.status = AiActionPlanStatus.EXECUTING;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markCompleted() {
        this.status = AiActionPlanStatus.COMPLETED;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markPartial() {
        this.status = AiActionPlanStatus.PARTIAL;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        this.status = AiActionPlanStatus.FAILED;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markCancelled() {
        this.status = AiActionPlanStatus.CANCELLED;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markStale() {
        this.status = AiActionPlanStatus.STALE;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public void markExpired() {
        this.status = AiActionPlanStatus.EXPIRED;
        this.version++;
        this.updatedAt = Instant.now();
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public UUID id()                          { return id; }
    public UUID requestId()                   { return requestId; }
    public int planNumber()                   { return planNumber; }
    public AiActionPlanStatus status()        { return status; }
    public String policyCode()                { return policyCode; }
    public int policyVersion()                { return policyVersion; }
    public String planHash()                  { return planHash; }
    public String contextHash()               { return contextHash; }
    public String sourceStateHash()           { return sourceStateHash; }
    public AiActionRiskLevel riskLevel()      { return riskLevel; }
    public AiActionExecutionMode executionMode() { return executionMode; }
    public boolean requiresConfirmation()     { return requiresConfirmation; }
    public int stepCount()                    { return stepCount; }
    public int targetCount()                  { return targetCount; }
    public String summary()                   { return summary; }
    public int version()                      { return version; }
    public Instant expiresAt()                { return expiresAt; }
    public Instant createdAt()                { return createdAt; }
    public Instant updatedAt()                { return updatedAt; }
}
