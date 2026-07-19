package com.company.scopery.modules.resourcecapacity.calendarexception.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.query.SearchCalendarExceptionQuery;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.response.CalendarExceptionResponse;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacitySortFields;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CalendarExceptionQueryService {

    private final CalendarExceptionRepository calendarExceptionRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityWorkspaceAuthorizationService authorizationService;

    public CalendarExceptionQueryService(CalendarExceptionRepository calendarExceptionRepository,
                                         WorkingCalendarRepository workingCalendarRepository,
                                         CapacityWorkspaceAuthorizationService authorizationService) {
        this.calendarExceptionRepository = calendarExceptionRepository;
        this.workingCalendarRepository = workingCalendarRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public CalendarExceptionResponse getException(UUID calendarId, UUID id) {
        WorkingCalendar calendar = requireCalendar(calendarId);
        authorizationService.requireCalendarView(calendar.workspaceId());

        CalendarException exception = calendarExceptionRepository.findById(id)
                .orElseThrow(() -> CapacityExceptions.exceptionNotFound(id));
        if (!exception.workingCalendarId().equals(calendarId)) {
            throw CapacityExceptions.exceptionNotFound(id);
        }
        return CalendarExceptionResponse.from(exception);
    }

    @Transactional(readOnly = true)
    public PageResult<CalendarExceptionResponse> searchExceptions(SearchCalendarExceptionQuery query) {
        WorkingCalendar calendar = requireCalendar(query.calendarId());
        authorizationService.requireCalendarView(calendar.workspaceId());

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), CapacitySortFields.EXCEPTION_DATE, true);
        return calendarExceptionRepository
                .search(query.calendarId(), query.from(), query.to(), pageQuery)
                .map(CalendarExceptionResponse::from);
    }

    private WorkingCalendar requireCalendar(UUID calendarId) {
        return workingCalendarRepository.findById(calendarId)
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(calendarId));
    }
}
