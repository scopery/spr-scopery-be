package com.company.scopery.modules.workspace.team.domain;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceTeamMember(
        UUID teamId,
        UUID userId,
        Instant createdAt) {

    public static WorkspaceTeamMember create(UUID teamId, UUID userId) {
        return new WorkspaceTeamMember(teamId, userId, Instant.now());
    }
}
