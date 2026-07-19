package com.company.scopery.modules.resourcecapacity.dayrule.domain.model;

import java.util.List;
import java.util.UUID;

public interface CalendarDayRuleRepository {

    List<CalendarDayRule> findByWorkingCalendarId(UUID workingCalendarId);

    List<CalendarDayRule> replaceAll(UUID workingCalendarId, List<CalendarDayRule> rules);

    void deleteByWorkingCalendarId(UUID workingCalendarId);
}
