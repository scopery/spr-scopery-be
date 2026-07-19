package com.company.scopery.modules.resourcecapacity.workingcalendar.application.service;

import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Bootstraps a workspace-scoped default working calendar so every new workspace has a usable
 * capacity baseline (Mon-Fri 8h, Asia/Ho_Chi_Minh) without requiring manual setup.
 */
@Service
public class DefaultWorkingCalendarSeeder {

    public static final String DEFAULT_CALENDAR_CODE = "DEFAULT_BUSINESS_CALENDAR";
    public static final String DEFAULT_CALENDAR_NAME = "Default Business Calendar";
    public static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
    private static final BigDecimal DEFAULT_WORKING_HOURS = new BigDecimal("8");
    private static final BigDecimal ZERO_HOURS = BigDecimal.ZERO;
    private static final LocalTime WORKDAY_START = LocalTime.of(9, 0);
    private static final LocalTime WORKDAY_END = LocalTime.of(17, 0);

    private final WorkingCalendarRepository workingCalendarRepository;
    private final CalendarDayRuleRepository dayRuleRepository;

    public DefaultWorkingCalendarSeeder(WorkingCalendarRepository workingCalendarRepository,
                                        CalendarDayRuleRepository dayRuleRepository) {
        this.workingCalendarRepository = workingCalendarRepository;
        this.dayRuleRepository = dayRuleRepository;
    }

    @Transactional
    public WorkingCalendar ensureDefaultCalendar(UUID workspaceId) {
        return workingCalendarRepository.findDefaultActiveByWorkspaceId(workspaceId)
                .orElseGet(() -> createDefaultCalendar(workspaceId));
    }

    private WorkingCalendar createDefaultCalendar(UUID workspaceId) {
        if (workingCalendarRepository.existsByWorkspaceIdAndCode(workspaceId, DEFAULT_CALENDAR_CODE)) {
            return workingCalendarRepository.findByWorkspaceIdAndCode(workspaceId, DEFAULT_CALENDAR_CODE)
                    .orElseThrow();
        }

        WorkingCalendar calendar = WorkingCalendar.create(
                workspaceId, DEFAULT_CALENDAR_CODE, DEFAULT_CALENDAR_NAME,
                "Automatically provisioned default working calendar", DEFAULT_TIMEZONE, true);
        WorkingCalendar saved = workingCalendarRepository.save(calendar);

        dayRuleRepository.replaceAll(saved.id(), buildDefaultDayRules(saved.id()));

        return saved;
    }

    private List<CalendarDayRule> buildDefaultDayRules(UUID calendarId) {
        return List.of(
                CalendarDayRule.create(calendarId, DayOfWeek.MONDAY, true, WORKDAY_START, WORKDAY_END, DEFAULT_WORKING_HOURS),
                CalendarDayRule.create(calendarId, DayOfWeek.TUESDAY, true, WORKDAY_START, WORKDAY_END, DEFAULT_WORKING_HOURS),
                CalendarDayRule.create(calendarId, DayOfWeek.WEDNESDAY, true, WORKDAY_START, WORKDAY_END, DEFAULT_WORKING_HOURS),
                CalendarDayRule.create(calendarId, DayOfWeek.THURSDAY, true, WORKDAY_START, WORKDAY_END, DEFAULT_WORKING_HOURS),
                CalendarDayRule.create(calendarId, DayOfWeek.FRIDAY, true, WORKDAY_START, WORKDAY_END, DEFAULT_WORKING_HOURS),
                CalendarDayRule.create(calendarId, DayOfWeek.SATURDAY, false, null, null, ZERO_HOURS),
                CalendarDayRule.create(calendarId, DayOfWeek.SUNDAY, false, null, null, ZERO_HOURS)
        );
    }
}
