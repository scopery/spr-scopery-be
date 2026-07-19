package com.company.scopery.modules.profitability.summary.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record ProfitSnapshot(UUID id, UUID workspaceId, UUID projectId, UUID profileId,
        BigDecimal baselineRevenue, BigDecimal forecastRevenue, BigDecimal baselineCost, BigDecimal forecastCost,
        BigDecimal forecastProfit, BigDecimal forecastMarginPercent, String profitabilityStatus,
        int version, Instant createdAt, Instant updatedAt) {
    public static ProfitSnapshot fromSummary(UUID profileId, ProjectProfitabilitySummary s) {
        Instant now = Instant.now();
        return new ProfitSnapshot(UUID.randomUUID(), s.workspaceId(), s.projectId(), profileId,
                s.baselineRevenue(), s.forecastRevenue(), s.baselineCost(), s.forecastCost(),
                s.forecastProfit(), s.forecastMarginPercent(), s.profitabilityStatus(), 0, now, now);
    }
}
