package com.company.scopery.modules.profitability.summary.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record ProjectProfitabilitySummary(UUID id, UUID workspaceId, UUID projectId, String currency,
        BigDecimal baselineRevenue, BigDecimal forecastRevenue, BigDecimal baselineCost, BigDecimal forecastCost,
        BigDecimal forecastProfit, BigDecimal forecastMarginPercent, String profitabilityStatus, Instant lastSnapshotAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static ProjectProfitabilitySummary create(UUID workspaceId, UUID projectId, String currency,
                                                     BigDecimal forecastRevenue, BigDecimal forecastCost,
                                                     BigDecimal forecastProfit, BigDecimal margin, String status) {
        Instant now = Instant.now();
        return new ProjectProfitabilitySummary(UUID.randomUUID(), workspaceId, projectId, currency,
                null, forecastRevenue, null, forecastCost, forecastProfit, margin, status, now, 0, now, now);
    }
    public ProjectProfitabilitySummary replace(BigDecimal forecastRevenue, BigDecimal forecastCost,
                                               BigDecimal forecastProfit, BigDecimal margin, String status) {
        Instant now = Instant.now();
        return new ProjectProfitabilitySummary(id, workspaceId, projectId, currency, baselineRevenue, forecastRevenue,
                baselineCost, forecastCost, forecastProfit, margin, status, now, version, createdAt, now);
    }
}
