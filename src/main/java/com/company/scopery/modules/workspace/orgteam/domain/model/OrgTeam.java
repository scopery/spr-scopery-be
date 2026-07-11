package com.company.scopery.modules.workspace.orgteam.domain.model;

import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus;
import com.company.scopery.modules.workspace.orgteam.domain.valueobject.OrgTeamCode;

import java.time.Instant;
import java.util.UUID;

public record OrgTeam(
        UUID id,
        UUID organizationId,
        OrgTeamCode code,
        String name,
        String description,
        OrgTeamStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static OrgTeam create(UUID organizationId, String name, OrgTeamCode code, String description) {
        Instant now = Instant.now();
        return new OrgTeam(UUID.randomUUID(), organizationId, code, name, description,
                OrgTeamStatus.ACTIVE, 0, now, now);
    }

    public OrgTeam update(String name, String description) {
        return new OrgTeam(id, organizationId, code, name, description, status, version, createdAt, Instant.now());
    }

    public OrgTeam activate() {
        return new OrgTeam(id, organizationId, code, name, description,
                OrgTeamStatus.ACTIVE, version, createdAt, Instant.now());
    }

    public OrgTeam archive() {
        return new OrgTeam(id, organizationId, code, name, description,
                OrgTeamStatus.ARCHIVED, version, createdAt, Instant.now());
    }
}
