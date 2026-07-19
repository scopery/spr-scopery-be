package com.company.scopery.modules.resourcecapacity.calendarexception.domain.model;

import com.company.scopery.modules.resourcecapacity.calendarexception.domain.enums.CalendarExceptionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CalendarException(
        UUID id,
        UUID workingCalendarId,
        LocalDate exceptionDate,
        CalendarExceptionType exceptionType,
        String name,
        String description,
        boolean isWorkingDay,
        BigDecimal workingHours,
        Instant createdAt,
        Instant updatedAt
) {

    public static CalendarException create(
            UUID workingCalendarId,
            LocalDate exceptionDate,
            CalendarExceptionType exceptionType,
            String name,
            String description,
            boolean isWorkingDay,
            BigDecimal workingHours) {
        boolean effectiveWorkingDay = exceptionType.isNonWorking() ? false : isWorkingDay;
        BigDecimal effectiveHours = exceptionType.isNonWorking() ? BigDecimal.ZERO : workingHours;
        return new CalendarException(
                UUID.randomUUID(), workingCalendarId, exceptionDate, exceptionType, name, description,
                effectiveWorkingDay, effectiveHours, null, null
        );
    }

    public CalendarException update(
            CalendarExceptionType exceptionType,
            String name,
            String description,
            boolean isWorkingDay,
            BigDecimal workingHours) {
        boolean effectiveWorkingDay = exceptionType.isNonWorking() ? false : isWorkingDay;
        BigDecimal effectiveHours = exceptionType.isNonWorking() ? BigDecimal.ZERO : workingHours;
        return new CalendarException(
                this.id, this.workingCalendarId, this.exceptionDate, exceptionType, name, description,
                effectiveWorkingDay, effectiveHours, this.createdAt, this.updatedAt
        );
    }
}
