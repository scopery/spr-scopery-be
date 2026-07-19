package com.company.scopery.modules.resourcecapacity.dayrule.http.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record DayRuleItemRequest(
        @NotNull DayOfWeek dayOfWeek,
        @NotNull Boolean isWorkingDay,
        LocalTime startTime,
        LocalTime endTime,
        @NotNull BigDecimal workingHours
) {}
