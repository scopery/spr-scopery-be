package com.company.scopery.modules.workspace.team.application.response;

import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a workspace team")
public record TeamResponse(
        @Schema(description = "Unique identifier of the team", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace this team belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "Unique short code of the team", example = "BE-SQUAD")
        String code,

        @Schema(description = "Display name of the team", example = "Backend Squad")
        String name,

        @Schema(description = "Description of the team", example = "Responsible for all backend services", nullable = true)
        String description,

        @Schema(description = "Current status of the team", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "Timestamp when the team was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the team was last updated", example = "2026-07-17T03:00:00.000000Z")
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
