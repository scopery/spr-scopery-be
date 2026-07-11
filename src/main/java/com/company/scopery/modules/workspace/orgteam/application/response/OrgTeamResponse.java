package com.company.scopery.modules.workspace.orgteam.application.response;

import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;

import java.time.Instant;
import java.util.UUID;

public record OrgTeamResponse(
        UUID id,
        UUID organizationId,
        String code,
        String name,
        String description,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static OrgTeamResponse from(OrgTeam team) {
        return new OrgTeamResponse(
                team.id(),
                team.organizationId(),
                team.code().value(),
                team.name(),
                team.description(),
                team.status().name(),
                team.version(),
                team.createdAt(),
                team.updatedAt());
    }
}
