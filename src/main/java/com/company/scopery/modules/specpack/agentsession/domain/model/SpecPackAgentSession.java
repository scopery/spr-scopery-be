package com.company.scopery.modules.specpack.agentsession.domain.model;

import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentSessionStatus;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageCode;
import com.company.scopery.modules.specpack.agentsession.domain.enums.ReadinessStatus;

import java.time.Instant;
import java.util.UUID;

public class SpecPackAgentSession {

    private UUID id;
    private UUID projectId;
    private UUID specPackId;
    private UUID scopePackageId;
    private AgentSessionStatus status;
    private AgentStageCode currentStageCode;
    private ReadinessStatus readinessStatus;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    private SpecPackAgentSession() {}

    public static SpecPackAgentSession create(UUID projectId, UUID specPackId, UUID scopePackageId) {
        SpecPackAgentSession s = new SpecPackAgentSession();
        s.id = UUID.randomUUID();
        s.projectId = projectId;
        s.specPackId = specPackId;
        s.scopePackageId = scopePackageId;
        s.status = AgentSessionStatus.ACTIVE;
        s.currentStageCode = AgentStageCode.PREPARE;
        s.readinessStatus = ReadinessStatus.NOT_READY;
        s.createdAt = Instant.now();
        s.updatedAt = s.createdAt;
        return s;
    }

    public static SpecPackAgentSession reconstitute(UUID id, UUID projectId, UUID specPackId, UUID scopePackageId,
                                                     AgentSessionStatus status, AgentStageCode currentStageCode,
                                                     ReadinessStatus readinessStatus, String createdBy,
                                                     Instant createdAt, Instant updatedAt) {
        SpecPackAgentSession s = new SpecPackAgentSession();
        s.id = id;
        s.projectId = projectId;
        s.specPackId = specPackId;
        s.scopePackageId = scopePackageId;
        s.status = status;
        s.currentStageCode = currentStageCode;
        s.readinessStatus = readinessStatus;
        s.createdBy = createdBy;
        s.createdAt = createdAt;
        s.updatedAt = updatedAt;
        return s;
    }

    public void advanceToStage(AgentStageCode next) {
        if (status != AgentSessionStatus.ACTIVE) throw new IllegalStateException("Session is not active");
        this.currentStageCode = next;
        this.updatedAt = Instant.now();
    }

    public void updateReadiness(ReadinessStatus readiness) {
        this.readinessStatus = readiness;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status != AgentSessionStatus.ACTIVE) throw new IllegalStateException("Session is not active");
        this.status = AgentSessionStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        this.status = AgentSessionStatus.COMPLETED;
        this.currentStageCode = AgentStageCode.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public UUID id()                          { return id; }
    public UUID projectId()                   { return projectId; }
    public UUID specPackId()                  { return specPackId; }
    public UUID scopePackageId()              { return scopePackageId; }
    public AgentSessionStatus status()        { return status; }
    public AgentStageCode currentStageCode()  { return currentStageCode; }
    public ReadinessStatus readinessStatus()  { return readinessStatus; }
    public String createdBy()                 { return createdBy; }
    public Instant createdAt()                { return createdAt; }
    public Instant updatedAt()                { return updatedAt; }
}
