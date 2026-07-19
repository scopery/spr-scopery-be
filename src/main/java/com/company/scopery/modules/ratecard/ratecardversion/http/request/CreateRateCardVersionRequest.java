package com.company.scopery.modules.ratecard.ratecardversion.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request body for creating a new rate card version")
public record CreateRateCardVersionRequest(
        @Schema(description = "Display name of the rate card version", example = "Q1 2026 Rates", nullable = true)
        String name,

        @Schema(description = "Optional description of the rate card version", example = "First quarter 2026 rate schedule", nullable = true)
        String description,

        @Schema(description = "Date from which this version's rates are effective", example = "2026-01-01")
        @NotNull LocalDate effectiveFrom,

        @Schema(description = "Date on which this version's rates expire (null if open-ended)", example = "2026-03-31", nullable = true)
        LocalDate effectiveTo
) {}
