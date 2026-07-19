package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserAvailabilityResponse(
        UUID workspaceId,
        UUID userId,
        LocalDate fromDate,
        LocalDate toDate,
        BigDecimal focusFactor,
        BigDecimal defaultDailyHours,
        boolean usingLazyDefaults,
        List<DailyCapacityEntry> dailyEntries,
        BigDecimal totalWorkingHours,
        BigDecimal totalFocusedHours
) {}
