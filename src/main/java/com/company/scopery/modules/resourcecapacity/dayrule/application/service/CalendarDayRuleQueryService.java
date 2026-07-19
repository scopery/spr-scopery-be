package com.company.scopery.modules.resourcecapacity.dayrule.application.service;

import com.company.scopery.modules.resourcecapacity.dayrule.application.response.CalendarDayRuleResponse;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CalendarDayRuleQueryService {

    private final CalendarDayRuleRepository dayRuleRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityWorkspaceAuthorizationService authorizationService;

    public CalendarDayRuleQueryService(CalendarDayRuleRepository dayRuleRepository,
                                       WorkingCalendarRepository workingCalendarRepository,
                                       CapacityWorkspaceAuthorizationService authorizationService) {
        this.dayRuleRepository = dayRuleRepository;
        this.workingCalendarRepository = workingCalendarRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public List<CalendarDayRuleResponse> getDayRules(UUID calendarId) {
        WorkingCalendar calendar = workingCalendarRepository.findById(calendarId)
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(calendarId));

        authorizationService.requireCalendarView(calendar.workspaceId());

        return dayRuleRepository.findByWorkingCalendarId(calendarId)
                .stream()
                .sorted(Comparator.comparing(rule -> rule.dayOfWeek().getValue()))
                .map(CalendarDayRuleResponse::from)
                .toList();
    }
}
