package com.company.scopery.modules.resourcecapacity.usercapacityprofile.http.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateUserCapacityProfileRequest(
        @NotNull UUID workspaceMemberId,
        @NotNull UUID workingCalendarId,
        @NotNull BigDecimal defaultDailyHours,
        @NotNull BigDecimal focusFactor,
        @NotNull LocalDate effectiveFrom,
        LocalDate effectiveTo
) {}
