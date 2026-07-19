package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectAllocationSummaryResponse(
        UUID projectId,
        LocalDate fromDate,
        LocalDate toDate,
        List<AllocationSummaryItem> allocations,
        BigDecimal totalAllocationPercent,
        int distinctUserCount
) {}
