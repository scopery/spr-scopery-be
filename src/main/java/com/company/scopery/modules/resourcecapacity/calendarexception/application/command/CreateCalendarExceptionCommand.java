package com.company.scopery.modules.resourcecapacity.calendarexception.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateCalendarExceptionCommand(
        UUID calendarId,
        LocalDate exceptionDate,
        String exceptionType,
        String name,
        String description,
        boolean isWorkingDay,
        BigDecimal workingHours
) {}
