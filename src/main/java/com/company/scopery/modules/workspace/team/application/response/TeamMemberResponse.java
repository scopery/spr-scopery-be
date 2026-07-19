package com.company.scopery.modules.workspace.team.application.response;

import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeamMember;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a team member entry")
public record TeamMemberResponse(
        @Schema(description = "ID of the team", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID teamId,

        @Schema(description = "ID of the user who is a team member", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID userId,

        @Schema(description = "Timestamp when the membership record was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    public static TeamMemberResponse from(WorkspaceTeamMember m) {
        return new TeamMemberResponse(m.teamId(), m.userId(), m.createdAt());
    }
}
