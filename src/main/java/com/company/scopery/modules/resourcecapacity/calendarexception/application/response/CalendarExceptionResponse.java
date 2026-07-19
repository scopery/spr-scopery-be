package com.company.scopery.modules.resourcecapacity.calendarexception.application.response;

import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CalendarExceptionResponse(
        UUID id,
        UUID workingCalendarId,
        LocalDate exceptionDate,
        String exceptionType,
        String name,
        String description,
        boolean isWorkingDay,
        BigDecimal workingHours
) {

    public static CalendarExceptionResponse from(CalendarException exception) {
        return new CalendarExceptionResponse(
                exception.id(),
                exception.workingCalendarId(),
                exception.exceptionDate(),
                exception.exceptionType().name(),
                exception.name(),
                exception.description(),
                exception.isWorkingDay(),
                exception.workingHours()
        );
    }
}
