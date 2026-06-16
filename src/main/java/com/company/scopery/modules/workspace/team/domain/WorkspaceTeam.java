package com.company.scopery.modules.workspace.team.domain;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceTeam(
        UUID id,
        UUID workspaceId,
        TeamCode code,
        String name,
        String description,
        TeamStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceTeam create(UUID workspaceId, String name, TeamCode code, String description) {
        Instant now = Instant.now();
        return new WorkspaceTeam(UUID.randomUUID(), workspaceId, code, name, description,
                TeamStatus.ACTIVE, now, now);
    }

    public WorkspaceTeam update(String name, String description) {
        return new WorkspaceTeam(id, workspaceId, code, name, description, status, createdAt, Instant.now());
    }

    public WorkspaceTeam activate() {
        return new WorkspaceTeam(id, workspaceId, code, name, description,
                TeamStatus.ACTIVE, createdAt, Instant.now());
    }

    public WorkspaceTeam archive() {
        return new WorkspaceTeam(id, workspaceId, code, name, description,
                TeamStatus.ARCHIVED, createdAt, Instant.now());
    }
}
