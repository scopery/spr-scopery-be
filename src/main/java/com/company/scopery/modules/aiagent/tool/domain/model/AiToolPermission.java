package com.company.scopery.modules.aiagent.tool.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AiToolPermission {

    private final UUID id;
    private final UUID toolId;
    private final String permissionCode;
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiToolPermission(UUID id, UUID toolId, String permissionCode, String description,
                             Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.toolId = toolId;
        this.permissionCode = permissionCode;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiToolPermission create(UUID toolId, String permissionCode, String description) {
        if (toolId == null) {
            throw new IllegalArgumentException("toolId is required");
        }
        if (permissionCode == null || permissionCode.isBlank()) {
            throw new IllegalArgumentException("permissionCode is required");
        }
        Instant now = Instant.now();
        return new AiToolPermission(UUID.randomUUID(), toolId, permissionCode.trim(),
                description == null || description.isBlank() ? null : description.trim(), now, now);
    }

    public static AiToolPermission reconstitute(UUID id, UUID toolId, String permissionCode, String description,
                                                Instant createdAt, Instant updatedAt) {
        return new AiToolPermission(id, toolId, permissionCode, description, createdAt, updatedAt);
    }

    public UUID id() { return id; }
    public UUID toolId() { return toolId; }
    public String permissionCode() { return permissionCode; }
    public String description() { return description; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
