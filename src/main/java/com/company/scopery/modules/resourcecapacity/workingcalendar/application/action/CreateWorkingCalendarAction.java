package com.company.scopery.modules.resourcecapacity.workingcalendar.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.CreateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.response.WorkingCalendarResponse;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class CreateWorkingCalendarAction {

    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_END = LocalTime.of(17, 0);
    private static final BigDecimal WORK_HOURS = new BigDecimal("8.00");
    private static final BigDecimal ZERO_HOURS = new BigDecimal("0.00");

    private final WorkingCalendarRepository workingCalendarRepository;
    private final CalendarDayRuleRepository dayRuleRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public CreateWorkingCalendarAction(WorkingCalendarRepository workingCalendarRepository,
                                       CalendarDayRuleRepository dayRuleRepository,
                                       WorkspaceRepository workspaceRepository,
                                       CapacityActivityLogger activityLogger,
                                       CapacityWorkspaceAuthorizationService authorizationService,
                                       CapacityPlatformPublisher platformPublisher) {
        this.workingCalendarRepository = workingCalendarRepository;
        this.dayRuleRepository = dayRuleRepository;
        this.workspaceRepository = workspaceRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public WorkingCalendarResponse execute(CreateWorkingCalendarCommand cmd) {
        authorizationService.requireCalendarCreate(cmd.workspaceId());

        Workspace workspace = workspaceRepository.findById(cmd.workspaceId())
                .orElseThrow(() -> CapacityExceptions.calendarWorkspaceNotFound(cmd.workspaceId()));
        if (workspace.status() != WorkspaceStatus.ACTIVE) {
            throw CapacityExceptions.calendarWorkspaceNotActive(cmd.workspaceId());
        }

        validateTimezone(cmd.timezone());

        if (workingCalendarRepository.existsByWorkspaceIdAndCode(cmd.workspaceId(), cmd.code())) {
            throw CapacityExceptions.calendarCodeAlreadyExists(cmd.code(), cmd.workspaceId());
        }

        boolean isDefault = Boolean.TRUE.equals(cmd.isDefault());
        if (isDefault) {
            clearExistingDefaults(cmd.workspaceId());
        }

        WorkingCalendar calendar = WorkingCalendar.create(
                cmd.workspaceId(), cmd.code(), cmd.name(), cmd.description(), cmd.timezone(), isDefault);
        WorkingCalendar saved = workingCalendarRepository.save(calendar);

        seedDefaultDayRules(saved.id());

        platformPublisher.enqueueCalendar(saved, "CAPACITY_CALENDAR_CREATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.WORKING_CALENDAR,
                saved.id(),
                CapacityActivityActions.CAPACITY_CALENDAR_CREATED,
                "Working calendar created: " + saved.code());

        return WorkingCalendarResponse.from(saved);
    }

    private void validateTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            throw CapacityExceptions.calendarInvalidTimezone(timezone);
        }
    }

    private void clearExistingDefaults(java.util.UUID workspaceId) {
        for (WorkingCalendar existing : workingCalendarRepository.findAllActiveDefaultsByWorkspaceId(workspaceId)) {
            WorkingCalendar cleared = workingCalendarRepository.save(existing.clearDefault());
            platformPublisher.audit(AuditEventType.CAPACITY_CALENDAR_DEFAULT_CHANGED, null,
                    CapacityPlatformPublisher.AGGREGATE_WORKING_CALENDAR, cleared.id(),
                    null, workspaceId, java.util.Map.of("isDefault", false),
                    "Default calendar cleared: " + cleared.code());
        }
    }

    private void seedDefaultDayRules(java.util.UUID calendarId) {
        List<CalendarDayRule> defaults = List.of(
                weekday(calendarId, DayOfWeek.MONDAY),
                weekday(calendarId, DayOfWeek.TUESDAY),
                weekday(calendarId, DayOfWeek.WEDNESDAY),
                weekday(calendarId, DayOfWeek.THURSDAY),
                weekday(calendarId, DayOfWeek.FRIDAY),
                weekend(calendarId, DayOfWeek.SATURDAY),
                weekend(calendarId, DayOfWeek.SUNDAY)
        );
        dayRuleRepository.replaceAll(calendarId, defaults);
    }

    private CalendarDayRule weekday(java.util.UUID calendarId, DayOfWeek day) {
        return CalendarDayRule.create(calendarId, day, true, WORK_START, WORK_END, WORK_HOURS);
    }

    private CalendarDayRule weekend(java.util.UUID calendarId, DayOfWeek day) {
        return CalendarDayRule.create(calendarId, day, false, null, null, ZERO_HOURS);
    }
}
