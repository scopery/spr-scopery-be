package com.company.scopery.modules.workspace.workspace.domain;

import java.time.Instant;
import java.util.UUID;

public record Workspace(
        UUID id,
        UUID organizationId,
        WorkspaceCode code,
        String name,
        String description,
        UUID ownerUserId,
        WorkspaceVisibility defaultVisibility,
        WorkspaceStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static Workspace create(UUID organizationId, String name, WorkspaceCode code, String description,
                                   UUID ownerUserId, WorkspaceVisibility defaultVisibility) {
        Instant now = Instant.now();
        return new Workspace(UUID.randomUUID(), organizationId, code, name, description,
                ownerUserId, defaultVisibility, WorkspaceStatus.ACTIVE, now, now);
    }

    public Workspace update(String name, String description, WorkspaceVisibility defaultVisibility) {
        return new Workspace(id, organizationId, code, name, description,
                ownerUserId, defaultVisibility, status, createdAt, Instant.now());
    }

    public Workspace activate() {
        return new Workspace(id, organizationId, code, name, description,
                ownerUserId, defaultVisibility, WorkspaceStatus.ACTIVE, createdAt, Instant.now());
    }

    public Workspace archive() {
        return new Workspace(id, organizationId, code, name, description,
                ownerUserId, defaultVisibility, WorkspaceStatus.ARCHIVED, createdAt, Instant.now());
    }
}
