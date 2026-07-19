package com.company.scopery.modules.reporting.dashboard.application.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CapacityReportResponse(
        Boolean sourceAvailable,
        Long allocatedUsers,
        BigDecimal averageAllocationPercent,
        Long overAllocatedUsers,
        BigDecimal allocatedCapacityHours,
        BigDecimal scheduledHours,
        BigDecimal capacityGapHours,
        List<UserCapacityRowResponse> userCapacityRows,
        Boolean privateLeaveDetailsMasked
) {
    public record UserCapacityRowResponse(
            UUID userId,
            BigDecimal allocationPercent,
            String allocationType,
            String startDate,
            String endDate
    ) {}
}
