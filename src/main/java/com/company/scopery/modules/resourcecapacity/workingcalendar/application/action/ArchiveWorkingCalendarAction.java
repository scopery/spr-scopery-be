package com.company.scopery.modules.resourcecapacity.workingcalendar.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.ArchiveWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.response.WorkingCalendarResponse;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveWorkingCalendarAction {

    private final WorkingCalendarRepository workingCalendarRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;
    private final CurrentUserAuthorizationService currentUserService;

    public ArchiveWorkingCalendarAction(WorkingCalendarRepository workingCalendarRepository,
                                        CapacityActivityLogger activityLogger,
                                        CapacityWorkspaceAuthorizationService authorizationService,
                                        CapacityPlatformPublisher platformPublisher,
                                        CurrentUserAuthorizationService currentUserService) {
        this.workingCalendarRepository = workingCalendarRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public WorkingCalendarResponse execute(ArchiveWorkingCalendarCommand cmd) {
        WorkingCalendar calendar = workingCalendarRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.calendarNotFound(cmd.id()));

        authorizationService.requireCalendarArchive(calendar.workspaceId());

        if (calendar.status() == WorkingCalendarStatus.ARCHIVED) {
            throw CapacityExceptions.calendarArchived(cmd.id());
        }

        if (workingCalendarRepository.isReferencedByCapacityProfiles(cmd.id())) {
            throw CapacityExceptions.calendarInUse(cmd.id());
        }

        var actorId = currentUserService.resolveCurrentUser().id();
        WorkingCalendar saved = workingCalendarRepository.save(calendar.archive(actorId));

        platformPublisher.enqueueCalendar(saved, "CAPACITY_CALENDAR_ARCHIVED");
        activityLogger.logSuccess(
                CapacityEntityTypes.WORKING_CALENDAR,
                saved.id(),
                CapacityActivityActions.CAPACITY_CALENDAR_ARCHIVED,
                "Working calendar archived: " + saved.code());

        return WorkingCalendarResponse.from(saved);
    }
}
