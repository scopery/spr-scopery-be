package com.company.scopery.modules.externalparty.relationship.application.response;

import com.company.scopery.modules.externalparty.relationship.domain.model.ProjectExternalPartyRelationship;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a relationship between a project and an external party")
public record ProjectExternalPartyRelationshipResponse(
        @Schema(description = "Unique identifier of the relationship record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the project", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID projectId,

        @Schema(description = "ID of the external organization", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID organizationId,

        @Schema(description = "Type of the relationship between the project and the external party", example = "PRIMARY_CLIENT", allowableValues = {"PRIMARY_CLIENT", "SUBCONTRACTOR", "PARTNER", "REGULATOR"})
        String relationshipType,

        @Schema(description = "Current status of the relationship", example = "ACTIVE")
        String status,

        @Schema(description = "Timestamp when the relationship was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    public static ProjectExternalPartyRelationshipResponse from(ProjectExternalPartyRelationship e) {
        return new ProjectExternalPartyRelationshipResponse(e.id(), e.projectId(), e.organizationId(), e.relationshipType(), e.status(), e.createdAt());
    }
}
