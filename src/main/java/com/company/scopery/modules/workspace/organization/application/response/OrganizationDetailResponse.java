package com.company.scopery.modules.workspace.organization.application.response;

import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Detailed representation of an organization")
public record OrganizationDetailResponse(
        @Schema(description = "Unique identifier of the organization", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique short code of the organization", example = "ACME")
        String code,

        @Schema(description = "Display name of the organization", example = "Acme Corporation")
        String name,

        @Schema(description = "Description of the organization", example = "Global leader in anvil manufacturing", nullable = true)
        String description,

        @Schema(description = "User ID of the organization owner", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID ownerUserId,

        @Schema(description = "Current status of the organization", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "Timestamp when the organization was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the organization was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static OrganizationDetailResponse from(Organization org) {
        return new OrganizationDetailResponse(
                org.id(),
                org.code().value(),
                org.name(),
                org.description(),
                org.ownerUserId(),
                org.status().name(),
                org.createdAt(),
                org.updatedAt());
    }
}
