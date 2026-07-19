package com.company.scopery.modules.resourcecapacity.workingcalendar.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacitySortFields;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.query.SearchWorkingCalendarQuery;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.response.WorkingCalendarResponse;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkingCalendarQueryService {

    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityWorkspaceAuthorizationService authorizationService;

    public WorkingCalendarQueryService(WorkingCalendarRepository workingCalendarRepository,
                                       CapacityWorkspaceAuthorizationService authorizationService) {
        this.workingCalendarRepository = workingCalendarRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public WorkingCalendarResponse getCalendar(UUID id) {
        WorkingCalendar calendar = workingCalendarRepository.findById(id)
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(id));
        authorizationService.requireCalendarView(calendar.workspaceId());
        return WorkingCalendarResponse.from(calendar);
    }

    @Transactional(readOnly = true)
    public PageResult<WorkingCalendarResponse> searchCalendars(SearchWorkingCalendarQuery query) {
        authorizationService.requireCalendarView(query.workspaceId());

        WorkingCalendarStatus status = CapacityEnumParser.parseOptional(
                WorkingCalendarStatus.class, query.status(),
                "CAPACITY_CALENDAR_INVALID_STATUS", "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), CapacitySortFields.CODE, true);

        return workingCalendarRepository
                .search(query.workspaceId(), status, query.isDefault(), query.code(), pageQuery)
                .map(WorkingCalendarResponse::from);
    }
}
