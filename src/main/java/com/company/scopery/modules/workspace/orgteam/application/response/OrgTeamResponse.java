package com.company.scopery.modules.workspace.orgteam.application.response;

import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of an organization-level team")
public record OrgTeamResponse(
        @Schema(description = "Unique identifier of the organization team", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the organization this team belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID organizationId,

        @Schema(description = "Unique short code of the organization team", example = "PLATFORM-ENG")
        String code,

        @Schema(description = "Display name of the organization team", example = "Platform Engineering")
        String name,

        @Schema(description = "Description of the organization team", example = "Owns the platform and infrastructure", nullable = true)
        String description,

        @Schema(description = "Current status of the organization team", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "Optimistic locking version", example = "1")
        int version,

        @Schema(description = "Timestamp when the organization team was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the organization team was last updated", example = "2026-07-17T03:00:00.000000Z")
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
