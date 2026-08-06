package com.company.scopery.modules.specpack.agentsession.domain.model;

import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageCode;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class SpecPackAgentStage {

    private UUID id;
    private UUID sessionId;
    private AgentStageCode stageCode;
    private AgentStageStatus stageStatus;
    private Map<String, Object> resultJson;
    private Instant completedAt;
    private Instant createdAt;

    private SpecPackAgentStage() {}

    public static SpecPackAgentStage createInitial(UUID sessionId, AgentStageCode stageCode) {
        SpecPackAgentStage s = new SpecPackAgentStage();
        s.id = UUID.randomUUID();
        s.sessionId = sessionId;
        s.stageCode = stageCode;
        s.stageStatus = AgentStageStatus.NOT_STARTED;
        s.createdAt = Instant.now();
        return s;
    }

    public static SpecPackAgentStage reconstitute(UUID id, UUID sessionId, AgentStageCode stageCode,
                                                   AgentStageStatus stageStatus, Map<String, Object> resultJson,
                                                   Instant completedAt, Instant createdAt) {
        SpecPackAgentStage s = new SpecPackAgentStage();
        s.id = id;
        s.sessionId = sessionId;
        s.stageCode = stageCode;
        s.stageStatus = stageStatus;
        s.resultJson = resultJson;
        s.completedAt = completedAt;
        s.createdAt = createdAt;
        return s;
    }

    public void start() {
        this.stageStatus = AgentStageStatus.IN_PROGRESS;
    }

    public void complete(Map<String, Object> result) {
        this.stageStatus = AgentStageStatus.COMPLETED;
        this.resultJson = result;
        this.completedAt = Instant.now();
    }

    public void waitForUser() {
        this.stageStatus = AgentStageStatus.WAITING_FOR_USER;
    }

    public UUID id()                          { return id; }
    public UUID sessionId()                   { return sessionId; }
    public AgentStageCode stageCode()         { return stageCode; }
    public AgentStageStatus stageStatus()     { return stageStatus; }
    public Map<String, Object> resultJson()   { return resultJson; }
    public Instant completedAt()              { return completedAt; }
    public Instant createdAt()                { return createdAt; }
}
