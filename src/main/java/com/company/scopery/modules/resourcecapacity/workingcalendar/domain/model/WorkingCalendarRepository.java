package com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkingCalendarRepository {

    WorkingCalendar save(WorkingCalendar calendar);

    Optional<WorkingCalendar> findById(UUID id);

    Optional<WorkingCalendar> findByWorkspaceIdAndCode(UUID workspaceId, String code);

    boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code);

    Optional<WorkingCalendar> findDefaultActiveByWorkspaceId(UUID workspaceId);

    List<WorkingCalendar> findAllActiveDefaultsByWorkspaceId(UUID workspaceId);

    boolean isReferencedByCapacityProfiles(UUID calendarId);

    PageResult<WorkingCalendar> search(UUID workspaceId, WorkingCalendarStatus status, Boolean isDefault,
                                       String code, PageQuery pageQuery);
}
