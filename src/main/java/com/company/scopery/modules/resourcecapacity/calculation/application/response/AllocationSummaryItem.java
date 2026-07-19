package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AllocationSummaryItem(
        UUID allocationId,
        UUID workspaceMemberId,
        UUID userId,
        BigDecimal allocationPercent,
        String allocationType,
        LocalDate startDate,
        LocalDate endDate
) {}
