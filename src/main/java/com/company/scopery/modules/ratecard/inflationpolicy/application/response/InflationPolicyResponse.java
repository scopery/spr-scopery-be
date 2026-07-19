package com.company.scopery.modules.ratecard.inflationpolicy.application.response;

import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicy;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Inflation policy details returned by the API")
public record InflationPolicyResponse(
        @Schema(description = "Unique identifier of the inflation policy", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique code identifying the inflation policy", example = "ANNUAL_CPI_2026")
        String code,

        @Schema(description = "Display name of the inflation policy", example = "Annual CPI 2026")
        String name,

        @Schema(description = "Optional description of the inflation policy", example = "Tracks CPI-based annual inflation adjustment", nullable = true)
        String description,

        @Schema(description = "Scope level of the inflation policy", example = "SYSTEM", allowableValues = {"SYSTEM", "ORGANIZATION", "WORKSPACE"})
        String scope,

        @Schema(description = "Organization this policy belongs to (null for system-scoped policies)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID organizationId,

        @Schema(description = "Workspace this policy belongs to (null for system/org-scoped policies)", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID workspaceId,

        @Schema(description = "Annual inflation rate as a percentage", example = "3.50")
        BigDecimal inflationPercent,

        @Schema(description = "Frequency at which inflation is compounded", example = "ANNUAL", allowableValues = {"ANNUAL", "MONTHLY", "NONE"})
        String compoundFrequency,

        @Schema(description = "Date from which this inflation policy takes effect", example = "2026-01-01")
        LocalDate effectiveFrom,

        @Schema(description = "Date on which this inflation policy expires (null if open-ended)", example = "2026-12-31", nullable = true)
        LocalDate effectiveTo,

        @Schema(description = "Current lifecycle status of the inflation policy", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "Timestamp when the policy was archived (null if not archived)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant archivedAt,

        @Schema(description = "ID of the user who archived the policy (null if not archived)", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID archivedBy,

        @Schema(description = "Optimistic locking version number", example = "1")
        int version,

        @Schema(description = "Timestamp when the policy was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the policy was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt
) {
    public static InflationPolicyResponse from(InflationPolicy p) {
        return new InflationPolicyResponse(p.id(), p.code(), p.name(), p.description(), p.scope().name(),
                p.organizationId(), p.workspaceId(), p.inflationPercent(), p.compoundFrequency().name(),
                p.effectiveFrom(), p.effectiveTo(), p.status().name(), p.archivedAt(), p.archivedBy(),
                p.version(), p.createdAt(), p.updatedAt());
    }
}
