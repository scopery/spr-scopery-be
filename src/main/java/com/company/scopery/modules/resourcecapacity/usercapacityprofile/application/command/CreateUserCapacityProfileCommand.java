package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateUserCapacityProfileCommand(
        UUID workspaceId,
        UUID workspaceMemberId,
        UUID workingCalendarId,
        BigDecimal defaultDailyHours,
        BigDecimal focusFactor,
        LocalDate effectiveFrom,
        LocalDate effectiveTo
) {}
