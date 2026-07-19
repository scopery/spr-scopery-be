package com.company.scopery.modules.resourcecapacity.workingcalendar.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.SetDefaultWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.response.WorkingCalendarResponse;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class SetDefaultWorkingCalendarAction {

    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public SetDefaultWorkingCalendarAction(WorkingCalendarRepository workingCalendarRepository,
                                           CapacityActivityLogger activityLogger,
                                           CapacityWorkspaceAuthorizationService authorizationService,
                                           CapacityPlatformPublisher platformPublisher) {
        this.workingCalendarRepository = workingCalendarRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public WorkingCalendarResponse execute(SetDefaultWorkingCalendarCommand cmd) {
        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.id()));

        authorizationService.requireCalendarUpdate(calendar.workspaceId());

        if (calendar.status() != WorkingCalendarStatus.ACTIVE) {
            throw CapacityExceptions.calendarNotActive(cmd.id());
        }

        for (WorkingCalendar existing : workingCalendarRepository.findAllActiveDefaultsByWorkspaceId(calendar.workspaceId())) {
            if (!existing.id().equals(calendar.id())) {
                workingCalendarRepository.save(existing.clearDefault());
            }
        }

        WorkingCalendar saved = workingCalendarRepository.save(calendar.setDefault());

        platformPublisher.enqueueCalendar(saved, "CAPACITY_CALENDAR_DEFAULT_CHANGED");
        platformPublisher.audit(AuditEventType.CAPACITY_CALENDAR_DEFAULT_CHANGED, null,
                CapacityPlatformPublisher.AGGREGATE_WORKING_CALENDAR, saved.id(),
                null, saved.workspaceId(), Map.of("isDefault", true),
                "Default calendar set: " + saved.code());
        activityLogger.logSuccess(
                CapacityEntityTypes.WORKING_CALENDAR,
                saved.id(),
                CapacityActivityActions.CAPACITY_CALENDAR_DEFAULT_CHANGED,
                "Default working calendar changed: " + saved.code());

        return WorkingCalendarResponse.from(saved);
    }
}
