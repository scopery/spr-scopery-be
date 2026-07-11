package com.company.scopery.modules.workspace.workspace.domain.model;

import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;

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
        WorkspaceJoinPolicy joinPolicy,
        WorkspaceStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static Workspace create(UUID organizationId, String name, WorkspaceCode code, String description,
                                   UUID ownerUserId, WorkspaceVisibility defaultVisibility,
                                   WorkspaceJoinPolicy joinPolicy) {
        Instant now = Instant.now();
        return new Workspace(UUID.randomUUID(), organizationId, code, name, description,
                ownerUserId, defaultVisibility, joinPolicy, WorkspaceStatus.ACTIVE, 0, now, now);
    }

    public Workspace update(String name, String description, WorkspaceVisibility defaultVisibility,
                            WorkspaceJoinPolicy joinPolicy) {
        return new Workspace(id, organizationId, code, name, description,
                ownerUserId, defaultVisibility, joinPolicy, status, version, createdAt, Instant.now());
    }

    public Workspace activate() {
        return new Workspace(id, organizationId, code, name, description,
                ownerUserId, defaultVisibility, joinPolicy, WorkspaceStatus.ACTIVE, version, createdAt, Instant.now());
    }

    public Workspace archive() {
        return new Workspace(id, organizationId, code, name, description,
                ownerUserId, defaultVisibility, joinPolicy, WorkspaceStatus.ARCHIVED, version, createdAt, Instant.now());
    }
}
