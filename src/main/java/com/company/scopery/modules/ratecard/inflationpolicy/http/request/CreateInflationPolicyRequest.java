package com.company.scopery.modules.ratecard.inflationpolicy.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request body for creating a new inflation policy")
public record CreateInflationPolicyRequest(
        @Schema(description = "Unique code identifying the inflation policy", example = "ANNUAL_CPI_2026")
        @NotBlank String code,

        @Schema(description = "Display name of the inflation policy", example = "Annual CPI 2026")
        @NotBlank String name,

        @Schema(description = "Optional description of the inflation policy", example = "Tracks CPI-based annual inflation adjustment", nullable = true)
        String description,

        @Schema(description = "Scope level of the inflation policy", example = "SYSTEM", allowableValues = {"SYSTEM", "ORGANIZATION", "WORKSPACE"})
        @NotBlank String scope,

        @Schema(description = "Organization this policy belongs to (required for ORGANIZATION scope)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID organizationId,

        @Schema(description = "Workspace this policy belongs to (required for WORKSPACE scope)", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID workspaceId,

        @Schema(description = "Annual inflation rate as a percentage", example = "3.50")
        @NotNull BigDecimal inflationPercent,

        @Schema(description = "Frequency at which inflation is compounded", example = "ANNUAL", allowableValues = {"ANNUAL", "MONTHLY", "NONE"})
        @NotBlank String compoundFrequency,

        @Schema(description = "Date from which this inflation policy takes effect", example = "2026-01-01")
        @NotNull LocalDate effectiveFrom,

        @Schema(description = "Date on which this inflation policy expires (null if open-ended)", example = "2026-12-31", nullable = true)
        LocalDate effectiveTo
) {}
