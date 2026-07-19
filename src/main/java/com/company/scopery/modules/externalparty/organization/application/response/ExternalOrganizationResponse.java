package com.company.scopery.modules.externalparty.organization.application.response;

import com.company.scopery.modules.externalparty.organization.domain.model.ExternalOrganization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of an external organization")
public record ExternalOrganizationResponse(
        @Schema(description = "Unique identifier of the external organization", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace this external organization belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "Unique short code of the external organization", example = "ACME-EXT", nullable = true)
        String code,

        @Schema(description = "Display name of the external organization", example = "Acme Corp External")
        String name,

        @Schema(description = "Type classification of the external organization", example = "CLIENT", allowableValues = {"CLIENT", "VENDOR", "PARTNER", "REGULATOR"})
        String organizationType,

        @Schema(description = "Current status of the external organization", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "Timestamp when the external organization was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    public static ExternalOrganizationResponse from(ExternalOrganization e) {
        return new ExternalOrganizationResponse(e.id(), e.workspaceId(), e.code(), e.name(), e.organizationType().name(), e.status().name(), e.createdAt());
    }
}
