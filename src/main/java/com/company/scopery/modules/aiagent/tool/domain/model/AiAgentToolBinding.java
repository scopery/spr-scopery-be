package com.company.scopery.modules.aiagent.tool.domain.model;

import com.company.scopery.modules.aiagent.tool.domain.enums.AiAgentToolBindingStatus;

import java.time.Instant;
import java.util.UUID;

public class AiAgentToolBinding {

    private final UUID id;
    private final UUID agentId;
    private final UUID toolId;
    private AiAgentToolBindingStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiAgentToolBinding(UUID id, UUID agentId, UUID toolId, AiAgentToolBindingStatus status,
                               Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.agentId = agentId;
        this.toolId = toolId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiAgentToolBinding create(UUID agentId, UUID toolId) {
        if (agentId == null) {
            throw new IllegalArgumentException("agentId is required");
        }
        if (toolId == null) {
            throw new IllegalArgumentException("toolId is required");
        }
        Instant now = Instant.now();
        return new AiAgentToolBinding(UUID.randomUUID(), agentId, toolId,
                AiAgentToolBindingStatus.ACTIVE, now, now);
    }

    public static AiAgentToolBinding reconstitute(UUID id, UUID agentId, UUID toolId,
                                                  AiAgentToolBindingStatus status,
                                                  Instant createdAt, Instant updatedAt) {
        return new AiAgentToolBinding(id, agentId, toolId, status, createdAt, updatedAt);
    }

    public void activate() {
        this.status = AiAgentToolBindingStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = AiAgentToolBindingStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID agentId() { return agentId; }
    public UUID toolId() { return toolId; }
    public AiAgentToolBindingStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
