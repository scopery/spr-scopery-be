package com.company.scopery.modules.ratecard.ratecardversion.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request body for updating an existing rate card version")
public record UpdateRateCardVersionRequest(
        @Schema(description = "Updated display name of the rate card version", example = "Q1 2026 Rates Revised", nullable = true)
        String name,

        @Schema(description = "Updated description of the rate card version", example = "Revised first quarter 2026 rate schedule", nullable = true)
        String description,

        @Schema(description = "Updated effective start date for this version's rates", example = "2026-01-01")
        @NotNull LocalDate effectiveFrom,

        @Schema(description = "Updated expiry date for this version's rates (null if open-ended)", example = "2026-03-31", nullable = true)
        LocalDate effectiveTo
) {}
