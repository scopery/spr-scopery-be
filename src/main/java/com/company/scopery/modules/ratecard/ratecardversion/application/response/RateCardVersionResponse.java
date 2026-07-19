package com.company.scopery.modules.ratecard.ratecardversion.application.response;

import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Rate card version details returned by the API")
public record RateCardVersionResponse(
        @Schema(description = "Unique identifier of the rate card version", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Rate card this version belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID rateCardId,

        @Schema(description = "Sequential version number within the rate card", example = "1")
        int versionNumber,

        @Schema(description = "Display name of the rate card version", example = "Q1 2026 Rates", nullable = true)
        String name,

        @Schema(description = "Optional description of the rate card version", example = "First quarter 2026 rate schedule", nullable = true)
        String description,

        @Schema(description = "Date from which this version's rates are effective", example = "2026-01-01")
        LocalDate effectiveFrom,

        @Schema(description = "Date on which this version's rates expire (null if open-ended)", example = "2026-03-31", nullable = true)
        LocalDate effectiveTo,

        @Schema(description = "Current lifecycle status of the rate card version", example = "DRAFT", allowableValues = {"DRAFT", "PUBLISHED", "ARCHIVED"})
        String status,

        @Schema(description = "Timestamp when the version was published (null if not yet published)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant publishedAt,

        @Schema(description = "ID of the user who published the version (null if not yet published)", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID publishedBy,

        @Schema(description = "Timestamp when the version was archived (null if not archived)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant archivedAt,

        @Schema(description = "ID of the user who archived the version (null if not archived)", example = "550e8400-e29b-41d4-a716-446655440004", nullable = true)
        UUID archivedBy,

        @Schema(description = "Optimistic locking version number", example = "1")
        int version,

        @Schema(description = "Timestamp when the rate card version was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the rate card version was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt
) {
    public static RateCardVersionResponse from(RateCardVersion v) {
        return new RateCardVersionResponse(v.id(), v.rateCardId(), v.versionNumber(), v.name(), v.description(),
                v.effectiveFrom(), v.effectiveTo(), v.status().name(), v.publishedAt(), v.publishedBy(),
                v.archivedAt(), v.archivedBy(), v.version(), v.createdAt(), v.updatedAt());
    }
}
