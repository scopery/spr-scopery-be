package com.company.scopery.modules.workspace.member.domain.model;

import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceMember(
        UUID id,
        UUID workspaceId,
        UUID userId,
        WorkspaceMemberStatus status,
        Instant joinedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceMember create(UUID workspaceId, UUID userId) {
        Instant now = Instant.now();
        return new WorkspaceMember(UUID.randomUUID(), workspaceId, userId,
                WorkspaceMemberStatus.ACTIVE, now, now, now);
    }

    public WorkspaceMember activate() {
        return new WorkspaceMember(id, workspaceId, userId,
                WorkspaceMemberStatus.ACTIVE, joinedAt, createdAt, Instant.now());
    }

    public WorkspaceMember deactivate() {
        return new WorkspaceMember(id, workspaceId, userId,
                WorkspaceMemberStatus.INACTIVE, joinedAt, createdAt, Instant.now());
    }
}
