package com.company.scopery.modules.workspace.orgteam.application.response;

import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMember;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a member entry in an organization team")
public record OrgTeamMemberResponse(
        @Schema(description = "ID of the organization team", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID teamId,

        @Schema(description = "ID of the user who is a team member", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID userId,

        @Schema(description = "Timestamp when the user joined the team", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant joinedAt) {

    public static OrgTeamMemberResponse from(OrgTeamMember member) {
        return new OrgTeamMemberResponse(member.teamId(), member.userId(), member.joinedAt());
    }
}
