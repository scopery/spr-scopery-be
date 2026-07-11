package com.company.scopery.modules.aiagent.prompt.domain.model;

import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.valueobject.PromptTemplateCode;

import java.time.Instant;
import java.util.UUID;

public class PromptTemplate {

    private final UUID id;
    private final UUID agentId;
    private String name;
    private final PromptTemplateCode code;
    private String description;
    private PromptTemplateStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private PromptTemplate(UUID id, UUID agentId, String name, PromptTemplateCode code,
                           String description, PromptTemplateStatus status,
                           Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.agentId = agentId;
        this.name = name;
        this.code = code;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PromptTemplate create(UUID agentId, String name, PromptTemplateCode code,
                                        String description) {
        validateName(name);
        Instant now = Instant.now();
        return new PromptTemplate(UUID.randomUUID(), agentId, name, code, description,
                PromptTemplateStatus.ACTIVE, now, now);
    }

    public static PromptTemplate reconstitute(UUID id, UUID agentId, String name,
                                              PromptTemplateCode code, String description,
                                              PromptTemplateStatus status,
                                              Instant createdAt, Instant updatedAt) {
        return new PromptTemplate(id, agentId, name, code, description, status, createdAt, updatedAt);
    }

    public void update(String name, String description) {
        validateName(name);
        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == PromptTemplateStatus.DEPRECATED) {
            throw new IllegalStateException("Deprecated prompt template cannot be activated again");
        }
        this.status = PromptTemplateStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = PromptTemplateStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Prompt template name is required");
        }
    }

    public UUID id() { return id; }
    public UUID agentId() { return agentId; }
    public String name() { return name; }
    public PromptTemplateCode code() { return code; }
    public String description() { return description; }
    public PromptTemplateStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
