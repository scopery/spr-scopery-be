package com.company.scopery.modules.resourcecapacity.planning.application.response;
import java.math.BigDecimal; import java.util.UUID;
public record UtilizationSummaryResponse(UUID resourceProfileId, BigDecimal availableCapacityHours, BigDecimal assignedEffortHours,
        BigDecimal plannedUtilizationPercent, BigDecimal overloadHours, BigDecimal underAllocationHours, String utilizationStatus) {}
