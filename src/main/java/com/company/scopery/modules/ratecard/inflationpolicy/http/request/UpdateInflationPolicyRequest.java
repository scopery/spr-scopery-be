package com.company.scopery.modules.ratecard.inflationpolicy.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request body for updating an existing inflation policy")
public record UpdateInflationPolicyRequest(
        @Schema(description = "Updated display name of the inflation policy", example = "Annual CPI 2026 Revised")
        @NotBlank String name,

        @Schema(description = "Updated description of the inflation policy", example = "Revised CPI-based annual inflation adjustment", nullable = true)
        String description,

        @Schema(description = "Updated annual inflation rate as a percentage", example = "3.50")
        @NotNull BigDecimal inflationPercent,

        @Schema(description = "Updated compounding frequency", example = "ANNUAL", allowableValues = {"ANNUAL", "MONTHLY", "NONE"})
        @NotBlank String compoundFrequency,

        @Schema(description = "Updated effective start date of the inflation policy", example = "2026-01-01")
        @NotNull LocalDate effectiveFrom,

        @Schema(description = "Updated expiry date of the inflation policy (null if open-ended)", example = "2026-12-31", nullable = true)
        LocalDate effectiveTo
) {}
