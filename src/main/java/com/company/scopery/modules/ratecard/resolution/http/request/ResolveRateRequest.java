package com.company.scopery.modules.ratecard.resolution.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request body for resolving the applicable rate for a given context and date")
public record ResolveRateRequest(
        @Schema(description = "Workspace context for rate resolution (null to resolve at organization or system level)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID workspaceId,

        @Schema(description = "Organization context for rate resolution (null to resolve at system level)", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID organizationId,

        @Schema(description = "Project context for rate resolution (used for PROJECT-scoped rate cards)", example = "550e8400-e29b-41d4-a716-446655440007", nullable = true)
        UUID projectId,

        @Schema(description = "Cost role to resolve the rate for (alternative to costRoleCode)", example = "550e8400-e29b-41d4-a716-446655440004", nullable = true)
        UUID costRoleId,

        @Schema(description = "Cost role code to resolve the rate for (alternative to costRoleId)", example = "SENIOR_DEV", nullable = true)
        String costRoleCode,

        @Schema(description = "Target date for which to resolve rates", example = "2026-07-17")
        @NotNull LocalDate targetDate,

        @Schema(description = "Currency code to use for rate resolution following ISO 4217 (defaults to rate card's default currency)", example = "USD", nullable = true)
        String currencyCode,

        @Schema(description = "Type of rate to resolve (e.g. COST, BILLING — null to resolve both)", example = "COST", nullable = true)
        String rateType
) {}
