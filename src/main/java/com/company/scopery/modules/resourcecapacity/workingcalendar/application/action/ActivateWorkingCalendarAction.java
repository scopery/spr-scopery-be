package com.company.scopery.modules.resourcecapacity.workingcalendar.application.action;

import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.ActivateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.response.WorkingCalendarResponse;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivateWorkingCalendarAction {

    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public ActivateWorkingCalendarAction(WorkingCalendarRepository workingCalendarRepository,
                                         CapacityActivityLogger activityLogger,
                                         CapacityWorkspaceAuthorizationService authorizationService,
                                         CapacityPlatformPublisher platformPublisher) {
        this.workingCalendarRepository = workingCalendarRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public WorkingCalendarResponse execute(ActivateWorkingCalendarCommand cmd) {
        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.id()));

        authorizationService.requireCalendarUpdate(calendar.workspaceId());

        if (calendar.status() == WorkingCalendarStatus.ARCHIVED) {
            throw CapacityExceptions.calendarArchived(cmd.id());
        }

        WorkingCalendar saved = workingCalendarRepository.save(calendar.activate());

        platformPublisher.enqueueCalendar(saved, "CAPACITY_CALENDAR_ACTIVATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.WORKING_CALENDAR,
                saved.id(),
                CapacityActivityActions.CAPACITY_CALENDAR_ACTIVATED,
                "Working calendar activated: " + saved.code());

        return WorkingCalendarResponse.from(saved);
    }
}
