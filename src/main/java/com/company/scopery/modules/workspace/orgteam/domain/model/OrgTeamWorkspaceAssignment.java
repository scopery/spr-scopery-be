package com.company.scopery.modules.workspace.orgteam.domain.model;

import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamWorkspaceAssignmentStatus;

import java.time.Instant;
import java.util.UUID;

public record OrgTeamWorkspaceAssignment(
        UUID id,
        UUID teamId,
        UUID workspaceId,
        UUID assignedBy,
        Instant assignedAt,
        OrgTeamWorkspaceAssignmentStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static OrgTeamWorkspaceAssignment create(UUID teamId, UUID workspaceId, UUID assignedBy) {
        Instant now = Instant.now();
        return new OrgTeamWorkspaceAssignment(UUID.randomUUID(), teamId, workspaceId, assignedBy,
                now, OrgTeamWorkspaceAssignmentStatus.ACTIVE, now, now);
    }

    public OrgTeamWorkspaceAssignment revoke() {
        return new OrgTeamWorkspaceAssignment(id, teamId, workspaceId, assignedBy, assignedAt,
                OrgTeamWorkspaceAssignmentStatus.REVOKED, createdAt, Instant.now());
    }
}
