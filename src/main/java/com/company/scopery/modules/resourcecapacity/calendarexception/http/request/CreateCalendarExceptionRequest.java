package com.company.scopery.modules.resourcecapacity.calendarexception.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCalendarExceptionRequest(
        @NotNull LocalDate exceptionDate,
        @NotBlank String exceptionType,
        @NotBlank String name,
        String description,
        boolean isWorkingDay,
        @NotNull BigDecimal workingHours
) {}
