package com.company.scopery.modules.resourcecapacity.planning.application.response;
import java.math.BigDecimal; import java.util.UUID;
public record ProjectCapacitySummaryResponse(UUID projectId, BigDecimal projectRequiredEffortHours,
        BigDecimal projectAllocatedCapacityHours, BigDecimal capacityGapHours, String forecastCompletionRisk,
        BigDecimal costForecastInput) {}
