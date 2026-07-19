package com.company.scopery.modules.ratecard.resolution.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request body for previewing the estimated labor cost of a task")
public record PreviewTaskRateRequest(
        @Schema(description = "Task to preview the rate for", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID taskId,

        @Schema(description = "Workspace context for rate resolution (null to use task's workspace)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID workspaceId,

        @Schema(description = "Cost role to use for rate resolution (overrides member's assigned cost role)", example = "550e8400-e29b-41d4-a716-446655440004", nullable = true)
        UUID costRoleId,

        @Schema(description = "Cost role code to use for rate resolution (alternative to costRoleId)", example = "SENIOR_DEV", nullable = true)
        String costRoleCode,

        @Schema(description = "Target date for which to resolve rates (defaults to today if null)", example = "2026-07-17", nullable = true)
        LocalDate targetDate,

        @Schema(description = "Currency code to use for rate resolution following ISO 4217 (defaults to rate card's default currency)", example = "USD", nullable = true)
        String currencyCode
) {}
