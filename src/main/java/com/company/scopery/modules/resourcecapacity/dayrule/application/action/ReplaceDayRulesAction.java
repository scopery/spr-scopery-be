package com.company.scopery.modules.resourcecapacity.dayrule.application.action;

import com.company.scopery.modules.resourcecapacity.dayrule.application.command.DayRuleItem;
import com.company.scopery.modules.resourcecapacity.dayrule.application.command.ReplaceDayRulesCommand;
import com.company.scopery.modules.resourcecapacity.dayrule.application.response.CalendarDayRuleResponse;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Component
public class ReplaceDayRulesAction {

    private final CalendarDayRuleRepository dayRuleRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityActivityLogger activityLogger;

    public ReplaceDayRulesAction(CalendarDayRuleRepository dayRuleRepository,
                                 WorkingCalendarRepository workingCalendarRepository,
                                 CapacityWorkspaceAuthorizationService authorizationService,
                                 CapacityActivityLogger activityLogger) {
        this.dayRuleRepository = dayRuleRepository;
        this.workingCalendarRepository = workingCalendarRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public List<CalendarDayRuleResponse> execute(ReplaceDayRulesCommand cmd) {
        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.calendarId())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.calendarId()));

        authorizationService.requireCalendarUpdate(calendar.workspaceId());

        List<DayRuleItem> items = cmd.dayRules();
        validate(cmd.calendarId(), items);

        List<CalendarDayRule> rules = items.stream()
                .map(item -> CalendarDayRule.create(
                        cmd.calendarId(),
                        item.dayOfWeek(),
                        item.isWorkingDay(),
                        item.startTime(),
                        item.endTime(),
                        item.workingHours()))
                .toList();

        List<CalendarDayRule> saved = dayRuleRepository.replaceAll(cmd.calendarId(), rules);

        activityLogger.logSuccess(
                CapacityEntityTypes.CALENDAR_DAY_RULE,
                cmd.calendarId(),
                CapacityActivityActions.CAPACITY_DAY_RULES_UPDATED,
                "Day rules replaced for calendar: " + cmd.calendarId()
        );

        return saved.stream().map(CalendarDayRuleResponse::from).toList();
    }

    private void validate(java.util.UUID calendarId, List<DayRuleItem> items) {
        if (items == null || items.size() != 7) {
            throw CapacityExceptions.dayRuleInvalidDay("day rules must cover all 7 days of week");
        }

        Set<DayOfWeek> seen = EnumSet.noneOf(DayOfWeek.class);
        boolean hasWorkingDay = false;

        for (DayRuleItem item : items) {
            if (item.dayOfWeek() == null) {
                throw CapacityExceptions.dayRuleInvalidDay("null");
            }
            if (!seen.add(item.dayOfWeek())) {
                throw CapacityExceptions.dayRuleDuplicate(item.dayOfWeek().name());
            }

            BigDecimal hours = item.workingHours();
            if (hours == null || hours.compareTo(BigDecimal.ZERO) < 0
                    || hours.compareTo(new BigDecimal("24")) > 0) {
                throw CapacityExceptions.dayRuleInvalidHours();
            }
            if (item.isWorkingDay()) {
                hasWorkingDay = true;
                if (hours.compareTo(BigDecimal.ZERO) <= 0) {
                    throw CapacityExceptions.dayRuleInvalidHours();
                }
            } else if (hours.compareTo(BigDecimal.ZERO) != 0) {
                throw CapacityExceptions.dayRuleInvalidHours();
            }
        }

        if (!hasWorkingDay) {
            throw CapacityExceptions.calendarNoWorkingDay(calendarId);
        }
    }
}
