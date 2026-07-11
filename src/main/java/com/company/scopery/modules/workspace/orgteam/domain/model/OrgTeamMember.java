package com.company.scopery.modules.workspace.orgteam.domain.model;

import java.time.Instant;
import java.util.UUID;

public record OrgTeamMember(
        UUID teamId,
        UUID userId,
        Instant joinedAt,
        Instant createdAt) {

    public static OrgTeamMember create(UUID teamId, UUID userId) {
        Instant now = Instant.now();
        return new OrgTeamMember(teamId, userId, now, now);
    }
}
