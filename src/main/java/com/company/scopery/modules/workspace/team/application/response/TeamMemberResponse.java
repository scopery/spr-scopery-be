package com.company.scopery.modules.workspace.team.application.response;

import com.company.scopery.modules.workspace.team.domain.WorkspaceTeamMember;

import java.time.Instant;
import java.util.UUID;

public record TeamMemberResponse(
        UUID teamId,
        UUID userId,
        Instant createdAt) {

    public static TeamMemberResponse from(WorkspaceTeamMember m) {
        return new TeamMemberResponse(m.teamId(), m.userId(), m.createdAt());
    }
}
