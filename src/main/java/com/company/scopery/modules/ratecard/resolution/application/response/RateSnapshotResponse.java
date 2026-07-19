package com.company.scopery.modules.ratecard.resolution.application.response;

import com.company.scopery.modules.ratecard.resolution.domain.RateSnapshot;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Resolved rate snapshot including base and inflation-adjusted rates")
public record RateSnapshotResponse(
        @Schema(description = "Rate card used to resolve the rate", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID rateCardId,

        @Schema(description = "Rate card version used to resolve the rate", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID rateCardVersionId,

        @Schema(description = "Specific rate card line that was matched", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID rateCardLineId,

        @Schema(description = "Cost role used for rate resolution", example = "550e8400-e29b-41d4-a716-446655440004")
        UUID costRoleId,

        @Schema(description = "Code of the cost role used for rate resolution", example = "SENIOR_DEV")
        String costRoleCode,

        @Schema(description = "Base cost rate per hour before inflation adjustment", example = "80.00")
        BigDecimal baseCostRate,

        @Schema(description = "Cost rate per hour after applying the inflation adjustment", example = "82.80")
        BigDecimal adjustedCostRate,

        @Schema(description = "Base billing rate per hour before inflation adjustment", example = "150.00", nullable = true)
        BigDecimal baseBillingRate,

        @Schema(description = "Billing rate per hour after applying the inflation adjustment", example = "155.25", nullable = true)
        BigDecimal adjustedBillingRate,

        @Schema(description = "Currency code of the resolved rates following ISO 4217", example = "USD")
        String currencyCode,

        @Schema(description = "Inflation policy applied during rate resolution (null if no inflation applied)", example = "550e8400-e29b-41d4-a716-446655440006", nullable = true)
        UUID inflationPolicyId,

        @Schema(description = "Inflation percentage applied (null if no inflation applied)", example = "3.50", nullable = true)
        BigDecimal inflationPercent,

        @Schema(description = "Number of years forward used for inflation compounding", example = "1.00")
        BigDecimal yearsForward,

        @Schema(description = "Date for which the rate was resolved", example = "2026-07-17")
        LocalDate resolvedForDate,

        @Schema(description = "Timestamp at which the rate resolution was computed", example = "2026-07-17T03:00:00.000000Z")
        Instant resolvedAt
) {
    public static RateSnapshotResponse from(RateSnapshot s) {
        return new RateSnapshotResponse(
                s.rateCardId(), s.rateCardVersionId(), s.rateCardLineId(),
                s.costRoleId(), s.costRoleCode(),
                s.baseCostRate(), s.adjustedCostRate(),
                s.baseBillingRate(), s.adjustedBillingRate(),
                s.currencyCode(), s.inflationPolicyId(), s.inflationPercent(),
                s.yearsForward(), s.resolvedForDate(), s.resolvedAt());
    }
}
