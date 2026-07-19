package com.company.scopery.modules.ratecard.ratecardline.application.response;

import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLine;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Rate card line details returned by the API")
public record RateCardLineResponse(
        @Schema(description = "Unique identifier of the rate card line", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Rate card version this line belongs to", example = "550e8400-e29b-41d4-a716-446655440005")
        UUID rateCardVersionId,

        @Schema(description = "Cost role associated with this rate card line", example = "550e8400-e29b-41d4-a716-446655440004")
        UUID costRoleId,

        @Schema(description = "Seniority level qualifier for the rate (e.g. Junior, Senior)", example = "Senior", nullable = true)
        String seniorityLevel,

        @Schema(description = "Location code qualifier for the rate (e.g. US-NYC, APAC)", example = "US-NYC", nullable = true)
        String locationCode,

        @Schema(description = "Currency code for the rates on this line following ISO 4217", example = "USD")
        String currencyCode,

        @Schema(description = "Internal cost rate per hour in the specified currency", example = "80.00")
        BigDecimal costRatePerHour,

        @Schema(description = "Billing rate per hour charged to the client in the specified currency", example = "150.00", nullable = true)
        BigDecimal billingRatePerHour,

        @Schema(description = "Optional notes or comments for this rate card line", example = "Includes benefits overhead", nullable = true)
        String notes,

        @Schema(description = "Optimistic locking version number", example = "1")
        int version,

        @Schema(description = "Timestamp when the rate card line was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the rate card line was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt
) {
    public static RateCardLineResponse from(RateCardLine l) {
        return new RateCardLineResponse(l.id(), l.rateCardVersionId(), l.costRoleId(), l.seniorityLevel(),
                l.locationCode(), l.currencyCode(), l.costRatePerHour(), l.billingRatePerHour(), l.notes(),
                l.version(), l.createdAt(), l.updatedAt());
    }
}
