package com.company.scopery.modules.ratecard.ratecard.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for updating an existing rate card")
public record UpdateRateCardRequest(
        @Schema(description = "Updated display name of the rate card", example = "Global Rate Card 2026 Revised")
        @NotBlank String name,

        @Schema(description = "Updated description of the rate card", example = "Revised standard global billing rates for 2026", nullable = true)
        String description,

        @Schema(description = "Updated default currency code following ISO 4217", example = "USD")
        @NotBlank String defaultCurrencyCode
) {}
