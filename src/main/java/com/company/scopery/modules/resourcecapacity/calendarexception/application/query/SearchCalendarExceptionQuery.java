package com.company.scopery.modules.resourcecapacity.calendarexception.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record SearchCalendarExceptionQuery(
        UUID calendarId,
        LocalDate from,
        LocalDate to,
        int page,
        int size
) {}
