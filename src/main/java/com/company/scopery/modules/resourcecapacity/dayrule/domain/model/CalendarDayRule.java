package com.company.scopery.modules.resourcecapacity.dayrule.domain.model;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record CalendarDayRule(
        UUID id,
        UUID workingCalendarId,
        DayOfWeek dayOfWeek,
        boolean isWorkingDay,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal workingHours,
        Instant createdAt,
        Instant updatedAt
) {

    public static CalendarDayRule create(
            UUID workingCalendarId,
            DayOfWeek dayOfWeek,
            boolean isWorkingDay,
            LocalTime startTime,
            LocalTime endTime,
            BigDecimal workingHours) {
        return new CalendarDayRule(
                UUID.randomUUID(),
                workingCalendarId,
                dayOfWeek,
                isWorkingDay,
                isWorkingDay ? startTime : null,
                isWorkingDay ? endTime : null,
                workingHours,
                null,
                null
        );
    }
}
