package com.company.scopery.modules.resourcecapacity.calendarexception.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.command.UpdateCalendarExceptionCommand;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.response.CalendarExceptionResponse;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.enums.CalendarExceptionType;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class UpdateCalendarExceptionAction {

    private final CalendarExceptionRepository calendarExceptionRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public UpdateCalendarExceptionAction(CalendarExceptionRepository calendarExceptionRepository,
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
    public CalendarExceptionResponse execute(UpdateCalendarExceptionCommand cmd) {
        CalendarException exception = calendarExceptionRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.exceptionNotFound(cmd.id()));

        if (!exception.workingCalendarId().equals(cmd.calendarId())) {
            throw CapacityExceptions.exceptionNotFound(cmd.id());
        }

        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.calendarId())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.calendarId()));

        authorizationService.requireCalendarUpdate(calendar.workspaceId());

        CalendarExceptionType type = CapacityEnumParser.parseRequired(
                CalendarExceptionType.class, cmd.exceptionType(),
                "CAPACITY_EXCEPTION_INVALID_TYPE", "exceptionType");

        validateHours(cmd.workingHours());

        CalendarException updated = exception.update(
                type, cmd.name(), cmd.description(), cmd.isWorkingDay(), cmd.workingHours());
        CalendarException saved = calendarExceptionRepository.save(updated);

        platformPublisher.enqueueException(saved, "CAPACITY_EXCEPTION_UPDATED");
        platformPublisher.audit(AuditEventType.CAPACITY_EXCEPTION_CHANGED, null,
                CapacityPlatformPublisher.AGGREGATE_CALENDAR_EXCEPTION, saved.id(),
                null, calendar.workspaceId(), Map.of("exceptionType", saved.exceptionType().name()),
                "Calendar exception updated: " + saved.exceptionDate());
        activityLogger.logSuccess(
                CapacityEntityTypes.CALENDAR_EXCEPTION,
                saved.id(),
                CapacityActivityActions.CAPACITY_EXCEPTION_UPDATED,
                "Calendar exception updated: " + saved.exceptionDate());

        return CalendarExceptionResponse.from(saved);
    }

    private void validateHours(BigDecimal hours) {
        if (hours == null || hours.compareTo(BigDecimal.ZERO) < 0 || hours.compareTo(new BigDecimal("24")) > 0) {
            throw CapacityExceptions.exceptionInvalidHours();
        }
    }
}
