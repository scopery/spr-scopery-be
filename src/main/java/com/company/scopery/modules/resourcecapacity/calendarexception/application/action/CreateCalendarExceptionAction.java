package com.company.scopery.modules.resourcecapacity.calendarexception.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.command.CreateCalendarExceptionCommand;
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
public class CreateCalendarExceptionAction {

    private final CalendarExceptionRepository calendarExceptionRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public CreateCalendarExceptionAction(CalendarExceptionRepository calendarExceptionRepository,
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
    public CalendarExceptionResponse execute(CreateCalendarExceptionCommand cmd) {
        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.calendarId())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.calendarId()));

        authorizationService.requireCalendarUpdate(calendar.workspaceId());

        CalendarExceptionType type = CapacityEnumParser.parseRequired(
                CalendarExceptionType.class, cmd.exceptionType(),
                "CAPACITY_EXCEPTION_INVALID_TYPE", "exceptionType");

        validateHours(cmd.workingHours());

        if (calendarExceptionRepository.existsByWorkingCalendarIdAndExceptionDate(cmd.calendarId(), cmd.exceptionDate())) {
            throw CapacityExceptions.exceptionDuplicateDate(cmd.calendarId(), cmd.exceptionDate().toString());
        }

        CalendarException exception = CalendarException.create(
                cmd.calendarId(), cmd.exceptionDate(), type, cmd.name(), cmd.description(),
                cmd.isWorkingDay(), cmd.workingHours());
        CalendarException saved = calendarExceptionRepository.save(exception);

        platformPublisher.enqueueException(saved, "CAPACITY_EXCEPTION_CREATED");
        platformPublisher.audit(AuditEventType.CAPACITY_EXCEPTION_CHANGED, null,
                CapacityPlatformPublisher.AGGREGATE_CALENDAR_EXCEPTION, saved.id(),
                null, calendar.workspaceId(), Map.of("exceptionType", saved.exceptionType().name()),
                "Calendar exception created: " + saved.exceptionDate());
        activityLogger.logSuccess(
                CapacityEntityTypes.CALENDAR_EXCEPTION,
                saved.id(),
                CapacityActivityActions.CAPACITY_EXCEPTION_CREATED,
                "Calendar exception created: " + saved.exceptionDate());

        return CalendarExceptionResponse.from(saved);
    }

    private void validateHours(BigDecimal hours) {
        if (hours == null || hours.compareTo(BigDecimal.ZERO) < 0 || hours.compareTo(new BigDecimal("24")) > 0) {
            throw CapacityExceptions.exceptionInvalidHours();
        }
    }
}
