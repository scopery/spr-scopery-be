package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CapacityCalculationResponse(
        UUID workspaceId,
        UUID userId,
        UUID projectId,
        LocalDate fromDate,
        LocalDate toDate,
        List<DailyCapacityEntry> dailyEntries,
        BigDecimal totalWorkingHours,
        BigDecimal totalFocusedHours,
        BigDecimal totalProjectAllocatedHours
) {}
