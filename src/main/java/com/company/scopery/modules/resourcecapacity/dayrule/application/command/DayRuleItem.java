package com.company.scopery.modules.resourcecapacity.dayrule.application.command;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record DayRuleItem(
        DayOfWeek dayOfWeek,
        boolean isWorkingDay,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal workingHours
) {}
