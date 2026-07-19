package com.company.scopery.modules.resourcecapacity.dayrule.application.response;

import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record CalendarDayRuleResponse(
        UUID id,
        UUID workingCalendarId,
        String dayOfWeek,
        boolean isWorkingDay,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal workingHours
) {

    public static CalendarDayRuleResponse from(CalendarDayRule rule) {
        return new CalendarDayRuleResponse(
                rule.id(),
                rule.workingCalendarId(),
                rule.dayOfWeek().name(),
                rule.isWorkingDay(),
                rule.startTime(),
                rule.endTime(),
                rule.workingHours()
        );
    }
}
