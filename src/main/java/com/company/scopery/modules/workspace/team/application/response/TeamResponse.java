package com.company.scopery.modules.workspace.team.application.response;

import com.company.scopery.modules.workspace.team.domain.WorkspaceTeam;

import java.time.Instant;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        UUID workspaceId,
        String code,
        String name,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static TeamResponse from(WorkspaceTeam t) {
        return new TeamResponse(
                t.id(),
                t.workspaceId(),
                t.code().value(),
                t.name(),
                t.description(),
                t.status().name(),
                t.createdAt(),
                t.updatedAt());
    }
}
