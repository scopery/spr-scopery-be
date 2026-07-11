package com.company.scopery.modules.aiagent.agent.domain.model;

import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import com.company.scopery.modules.aiagent.agent.domain.valueobject.AgentCode;

import java.time.Instant;
import java.util.UUID;

public class Agent {

    private final UUID id;
    private String name;
    private final AgentCode code;
    private AgentType type;
    private String description;
    private UUID defaultModelDeploymentId;
    private AgentOutputFormat outputFormat;
    private AgentStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Agent(UUID id, String name, AgentCode code, AgentType type, String description,
                  UUID defaultModelDeploymentId, AgentOutputFormat outputFormat,
                  AgentStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.type = type;
        this.description = description;
        this.defaultModelDeploymentId = defaultModelDeploymentId;
        this.outputFormat = outputFormat;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Agent create(String name, AgentCode code, AgentType type, String description,
                               UUID defaultModelDeploymentId, AgentOutputFormat outputFormat) {
        validateName(name);
        Instant now = Instant.now();
        return new Agent(UUID.randomUUID(), name, code, type, description,
                defaultModelDeploymentId, outputFormat, AgentStatus.ACTIVE, now, now);
    }

    public static Agent reconstitute(UUID id, String name, AgentCode code, AgentType type,
                                     String description, UUID defaultModelDeploymentId,
                                     AgentOutputFormat outputFormat, AgentStatus status,
                                     Instant createdAt, Instant updatedAt) {
        return new Agent(id, name, code, type, description, defaultModelDeploymentId,
                outputFormat, status, createdAt, updatedAt);
    }

    public void update(String name, AgentType type, String description,
                       UUID defaultModelDeploymentId, AgentOutputFormat outputFormat) {
        validateName(name);
        this.name = name;
        this.type = type;
        this.description = description;
        this.defaultModelDeploymentId = defaultModelDeploymentId;
        this.outputFormat = outputFormat;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == AgentStatus.DEPRECATED) {
            throw new IllegalStateException("Deprecated agent cannot be activated again");
        }
        this.status = AgentStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = AgentStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Agent name is required");
        }
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public AgentCode code() { return code; }
    public AgentType type() { return type; }
    public String description() { return description; }
    public UUID defaultModelDeploymentId() { return defaultModelDeploymentId; }
    public AgentOutputFormat outputFormat() { return outputFormat; }
    public AgentStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
