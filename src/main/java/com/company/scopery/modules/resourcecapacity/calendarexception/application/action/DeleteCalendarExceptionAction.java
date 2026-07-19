package com.company.scopery.modules.resourcecapacity.calendarexception.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.command.DeleteCalendarExceptionCommand;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class DeleteCalendarExceptionAction {

    private final CalendarExceptionRepository calendarExceptionRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public DeleteCalendarExceptionAction(CalendarExceptionRepository calendarExceptionRepository,
                                         WorkingCalendarRepository workingCalendarRepository,
                                         CapacityActivityLogger activityLogger,
                                         CapacityWorkspaceAuthorizationService authorizationService,
                                         CapacityPlatformPublisher platformPublisher) {
        this.calendarExceptionRepository = calendarExceptionRepository;
        this.workingCalendarRepository = workingCalendarRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public void execute(DeleteCalendarExceptionCommand cmd) {
        CalendarException exception = calendarExceptionRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.exceptionNotFound(cmd.id()));

        if (!exception.workingCalendarId().equals(cmd.calendarId())) {
            throw CapacityExceptions.exceptionNotFound(cmd.id());
        }

        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.calendarId())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.calendarId()));

        authorizationService.requireCalendarUpdate(calendar.workspaceId());

        calendarExceptionRepository.deleteById(cmd.id());

        platformPublisher.enqueueException(exception, "CAPACITY_EXCEPTION_DELETED");
        platformPublisher.audit(AuditEventType.CAPACITY_EXCEPTION_CHANGED, null,
                CapacityPlatformPublisher.AGGREGATE_CALENDAR_EXCEPTION, exception.id(),
                null, calendar.workspaceId(), Map.of("deleted", true),
                "Calendar exception deleted: " + exception.exceptionDate());
        activityLogger.logSuccess(
                CapacityEntityTypes.CALENDAR_EXCEPTION,
                exception.id(),
                CapacityActivityActions.CAPACITY_EXCEPTION_DELETED,
                "Calendar exception deleted: " + exception.exceptionDate());
    }
}
