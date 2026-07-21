package com.company.scopery.modules.aiaction.tool.domain.model;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionInvocationScope;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionToolPolicyStatus;

import java.time.Instant;
import java.util.UUID;

public class AiActionToolPolicy {

    private final UUID id;
    private final String toolCode;
    private final String toolVersion;
    private final AiActionInvocationScope invocationScope;
    private final AiActionRiskLevel riskLevel;
    private final AiActionExecutionMode executionMode;
    private final int maxBatchTargets;
    private final boolean dryRunRequired;
    private final boolean supportsCompensation;
    private final boolean supportsPause;
    private AiActionToolPolicyStatus status;
    private final Instant createdAt;

    private AiActionToolPolicy(UUID id, String toolCode, String toolVersion,
                                AiActionInvocationScope invocationScope, AiActionRiskLevel riskLevel,
                                AiActionExecutionMode executionMode, int maxBatchTargets,
                                boolean dryRunRequired, boolean supportsCompensation,
                                boolean supportsPause, AiActionToolPolicyStatus status, Instant createdAt) {
        this.id = id;
        this.toolCode = toolCode;
        this.toolVersion = toolVersion;
        this.invocationScope = invocationScope;
        this.riskLevel = riskLevel;
        this.executionMode = executionMode;
        this.maxBatchTargets = maxBatchTargets;
        this.dryRunRequired = dryRunRequired;
        this.supportsCompensation = supportsCompensation;
        this.supportsPause = supportsPause;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static AiActionToolPolicy create(String toolCode, String toolVersion,
                                             AiActionInvocationScope invocationScope,
                                             AiActionRiskLevel riskLevel, AiActionExecutionMode executionMode,
                                             int maxBatchTargets, boolean dryRunRequired,
                                             boolean supportsCompensation, boolean supportsPause) {
        return new AiActionToolPolicy(UUID.randomUUID(), toolCode, toolVersion, invocationScope,
                riskLevel, executionMode, maxBatchTargets, dryRunRequired, supportsCompensation,
                supportsPause, AiActionToolPolicyStatus.INACTIVE, Instant.now());
    }

    public static AiActionToolPolicy reconstitute(UUID id, String toolCode, String toolVersion,
                                                   AiActionInvocationScope invocationScope,
                                                   AiActionRiskLevel riskLevel,
                                                   AiActionExecutionMode executionMode, int maxBatchTargets,
                                                   boolean dryRunRequired, boolean supportsCompensation,
                                                   boolean supportsPause, AiActionToolPolicyStatus status,
                                                   Instant createdAt) {
        return new AiActionToolPolicy(id, toolCode, toolVersion, invocationScope, riskLevel,
                executionMode, maxBatchTargets, dryRunRequired, supportsCompensation,
                supportsPause, status, createdAt);
    }

    public void activate() {
        this.status = AiActionToolPolicyStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = AiActionToolPolicyStatus.INACTIVE;
    }

    public boolean isActive()                         { return status == AiActionToolPolicyStatus.ACTIVE; }

    public UUID id()                                  { return id; }
    public String toolCode()                          { return toolCode; }
    public String toolVersion()                       { return toolVersion; }
    public AiActionInvocationScope invocationScope()  { return invocationScope; }
    public AiActionRiskLevel riskLevel()              { return riskLevel; }
    public AiActionExecutionMode executionMode()      { return executionMode; }
    public int maxBatchTargets()                      { return maxBatchTargets; }
    public boolean dryRunRequired()                   { return dryRunRequired; }
    public boolean supportsCompensation()             { return supportsCompensation; }
    public boolean supportsPause()                    { return supportsPause; }
    public AiActionToolPolicyStatus status()          { return status; }
    public Instant createdAt()                        { return createdAt; }
}
