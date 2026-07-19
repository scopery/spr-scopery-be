package com.company.scopery.modules.resourcecapacity.workingcalendar.application.action;

import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.UpdateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.response.WorkingCalendarResponse;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Component
public class UpdateWorkingCalendarAction {

    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public UpdateWorkingCalendarAction(WorkingCalendarRepository workingCalendarRepository,
                                       CapacityActivityLogger activityLogger,
                                       CapacityWorkspaceAuthorizationService authorizationService,
                                       CapacityPlatformPublisher platformPublisher) {
        this.workingCalendarRepository = workingCalendarRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public WorkingCalendarResponse execute(UpdateWorkingCalendarCommand cmd) {
        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.id()));

        authorizationService.requireCalendarUpdate(calendar.workspaceId());

        String timezone = cmd.timezone() != null ? cmd.timezone() : calendar.timezone();
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            throw CapacityExceptions.calendarInvalidTimezone(timezone);
        }

        WorkingCalendar updated = calendar.update(
                cmd.name() != null ? cmd.name() : calendar.name(),
                cmd.description() != null ? cmd.description() : calendar.description(),
                timezone);
        WorkingCalendar saved = workingCalendarRepository.save(updated);

        platformPublisher.enqueueCalendar(saved, "CAPACITY_CALENDAR_UPDATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.WORKING_CALENDAR,
                saved.id(),
                CapacityActivityActions.CAPACITY_CALENDAR_UPDATED,
                "Working calendar updated: " + saved.code());

        return WorkingCalendarResponse.from(saved);
    }
}
