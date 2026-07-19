package com.company.scopery.modules.profitability.plan.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitabilityPlanVersion(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID profitabilityPlanId,
        int versionNumber,
        String versionLabel,
        String currency,
        BigDecimal baselineRevenue,
        BigDecimal baselineCost,
        BigDecimal baselineProfit,
        BigDecimal baselineMarginPercent,
        BigDecimal plannedRevenue,
        BigDecimal plannedCost,
        BigDecimal plannedProfit,
        BigDecimal plannedMarginPercent,
        String assumptionNotes,
        UUID sourceQuoteVersionId,
        UUID sourceBaselineId,
        boolean finalizedFlag,
        Instant finalizedAt,
        UUID finalizedBy,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitabilityPlanVersion create(
            UUID workspaceId,
            UUID projectId,
            UUID planId,
            int versionNumber,
            String versionLabel,
            String currency,
            BigDecimal baselineRevenue,
            BigDecimal baselineCost,
            BigDecimal baselineProfit,
            BigDecimal baselineMarginPercent,
            BigDecimal plannedRevenue,
            BigDecimal plannedCost,
            BigDecimal plannedProfit,
            BigDecimal plannedMarginPercent,
            String assumptionNotes) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        Instant now = Instant.now();
        BigDecimal zero = BigDecimal.ZERO;
        return new ProfitabilityPlanVersion(
                UUID.randomUUID(), workspaceId, projectId, planId,
                versionNumber, versionLabel, currency.trim(),
                baselineRevenue != null ? baselineRevenue : zero,
                baselineCost != null ? baselineCost : zero,
                baselineProfit != null ? baselineProfit : zero,
                baselineMarginPercent,
                plannedRevenue != null ? plannedRevenue : zero,
                plannedCost != null ? plannedCost : zero,
                plannedProfit != null ? plannedProfit : zero,
                plannedMarginPercent,
                assumptionNotes, null, null,
                false, null, null, "DRAFT", 0, now, now);
    }

    public ProfitabilityPlanVersion finalize(UUID actorId) {
        if (finalizedFlag) {
            throw new IllegalStateException("Plan version is already finalized");
        }
        Instant now = Instant.now();
        return new ProfitabilityPlanVersion(
                id, workspaceId, projectId, profitabilityPlanId,
                versionNumber, versionLabel, currency,
                baselineRevenue, baselineCost, baselineProfit, baselineMarginPercent,
                plannedRevenue, plannedCost, plannedProfit, plannedMarginPercent,
                assumptionNotes, sourceQuoteVersionId, sourceBaselineId,
                true, now, actorId, "FINALIZED", version, createdAt, now);
    }
}
