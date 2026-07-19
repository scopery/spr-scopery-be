package com.company.scopery.modules.ratecard.ratecard.application.response;

import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCard;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Rate card details returned by the API")
public record RateCardResponse(
        @Schema(description = "Unique identifier of the rate card", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique code identifying the rate card", example = "RC_GLOBAL_2026")
        String code,

        @Schema(description = "Display name of the rate card", example = "Global Rate Card 2026")
        String name,

        @Schema(description = "Optional description of the rate card", example = "Standard global billing rates for 2026", nullable = true)
        String description,

        @Schema(description = "Scope level of the rate card", example = "SYSTEM", allowableValues = {"SYSTEM", "ORGANIZATION", "WORKSPACE", "CLIENT", "PROJECT"})
        String scope,

        @Schema(description = "Organization this rate card belongs to (null for system-scoped rate cards)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID organizationId,

        @Schema(description = "Workspace this rate card belongs to (null for system/org-scoped rate cards)", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID workspaceId,

        @Schema(description = "Default currency code for the rate card following ISO 4217", example = "USD")
        String defaultCurrencyCode,

        @Schema(description = "Whether this is the default rate card for its scope", example = "false")
        boolean isDefault,

        @Schema(description = "Current lifecycle status of the rate card", example = "ACTIVE", allowableValues = {"DRAFT", "ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "ID of the currently active version of this rate card (null if no published version)", example = "550e8400-e29b-41d4-a716-446655440005", nullable = true)
        UUID currentVersionId,

        @Schema(description = "Whether this rate card is a built-in system rate card", example = "false")
        boolean builtIn,

        @Schema(description = "Timestamp when the rate card was archived (null if not archived)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant archivedAt,

        @Schema(description = "ID of the user who archived the rate card (null if not archived)", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID archivedBy,

        @Schema(description = "Optimistic locking version number", example = "1")
        int version,

        @Schema(description = "Timestamp when the rate card was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the rate card was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt
) {
    public static RateCardResponse from(RateCard c) {
        return new RateCardResponse(c.id(), c.code(), c.name(), c.description(), c.scope().name(),
                c.organizationId(), c.workspaceId(), c.defaultCurrencyCode(), c.isDefault(),
                c.status().name(), c.currentVersionId(), c.builtIn(),
                c.archivedAt(), c.archivedBy(), c.version(), c.createdAt(), c.updatedAt());
    }
}
