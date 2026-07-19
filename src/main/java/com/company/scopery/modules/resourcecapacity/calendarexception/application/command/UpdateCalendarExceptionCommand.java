package com.company.scopery.modules.resourcecapacity.calendarexception.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateCalendarExceptionCommand(
        UUID id,
        UUID calendarId,
        String exceptionType,
        String name,
        String description,
        boolean isWorkingDay,
        BigDecimal workingHours
) {}
