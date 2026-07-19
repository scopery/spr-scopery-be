package com.company.scopery.modules.aiagent.tool.domain.model;

import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolMutationType;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolStatus;
import com.company.scopery.modules.aiagent.tool.domain.valueobject.AiToolCode;

import java.time.Instant;
import java.util.UUID;

public class AiTool {

    private final UUID id;
    private final AiToolCode code;
    private String name;
    private String description;
    private String category;
    private AiToolMutationType mutationType;
    private boolean requiresHumanApproval;
    private AiToolStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiTool(UUID id, AiToolCode code, String name, String description, String category,
                   AiToolMutationType mutationType, boolean requiresHumanApproval,
                   AiToolStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.mutationType = mutationType;
        this.requiresHumanApproval = requiresHumanApproval;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiTool create(AiToolCode code, String name, String description, String category,
                                AiToolMutationType mutationType, boolean requiresHumanApproval) {
        validateName(name);
        validateCategory(category);
        if (mutationType == null) {
            throw new IllegalArgumentException("mutationType is required");
        }
        Instant now = Instant.now();
        return new AiTool(UUID.randomUUID(), code, name.trim(), trimOrNull(description), category.trim(),
                mutationType, requiresHumanApproval, AiToolStatus.ACTIVE, now, now);
    }

    public static AiTool reconstitute(UUID id, AiToolCode code, String name, String description, String category,
                                      AiToolMutationType mutationType, boolean requiresHumanApproval,
                                      AiToolStatus status, Instant createdAt, Instant updatedAt) {
        return new AiTool(id, code, name, description, category, mutationType, requiresHumanApproval,
                status, createdAt, updatedAt);
    }

    public void update(String name, String description, String category,
                       AiToolMutationType mutationType, boolean requiresHumanApproval) {
        validateName(name);
        validateCategory(category);
        if (mutationType == null) {
            throw new IllegalArgumentException("mutationType is required");
        }
        this.name = name.trim();
        this.description = trimOrNull(description);
        this.category = category.trim();
        this.mutationType = mutationType;
        this.requiresHumanApproval = requiresHumanApproval;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (status == AiToolStatus.DEPRECATED) {
            throw new IllegalStateException("Deprecated AI tool cannot be activated");
        }
        this.status = AiToolStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = AiToolStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deprecate() {
        this.status = AiToolStatus.DEPRECATED;
        this.updatedAt = Instant.now();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("AI tool name is required");
        }
    }

    private static void validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("AI tool category is required");
        }
    }

    private static String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public UUID id() { return id; }
    public AiToolCode code() { return code; }
    public String name() { return name; }
    public String description() { return description; }
    public String category() { return category; }
    public AiToolMutationType mutationType() { return mutationType; }
    public boolean requiresHumanApproval() { return requiresHumanApproval; }
    public AiToolStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
