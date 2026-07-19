package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyCapacityEntry(
        LocalDate date,
        boolean isWorkingDay,
        BigDecimal workingHours,
        BigDecimal focusedHours,
        BigDecimal projectAllocatedHours
) {}
