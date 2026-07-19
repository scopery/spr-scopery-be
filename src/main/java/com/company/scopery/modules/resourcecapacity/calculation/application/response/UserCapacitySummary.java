package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.math.BigDecimal;
import java.util.UUID;

public record UserCapacitySummary(
        UUID userId,
        UUID workspaceMemberId,
        BigDecimal totalWorkingHours,
        BigDecimal totalFocusedHours,
        BigDecimal totalAllocationPercent,
        boolean overAllocated
) {}
