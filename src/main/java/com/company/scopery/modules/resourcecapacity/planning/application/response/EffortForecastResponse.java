package com.company.scopery.modules.resourcecapacity.planning.application.response;
import java.math.BigDecimal; import java.util.UUID;
public record EffortForecastResponse(UUID projectId, BigDecimal forecastEffortHours, BigDecimal actualObservedHours,
        BigDecimal remainingEffortHours, String status) {}
