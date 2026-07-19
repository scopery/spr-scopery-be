package com.company.scopery.modules.projectnotification.reminder.application.action;

import com.company.scopery.modules.projectnotification.reminder.application.response.ProjectReminderRunResponse;
import com.company.scopery.modules.projectnotification.reminder.application.service.ProjectDueDateReminderService;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RunProjectReminderAction {
    private final ProjectDueDateReminderService reminderService;
    private final ProjectNotificationAuthorizationService authorization;

    public RunProjectReminderAction(ProjectDueDateReminderService reminderService,
                                    ProjectNotificationAuthorizationService authorization) {
        this.reminderService = reminderService;
        this.authorization = authorization;
    }

    @Transactional
    public ProjectReminderRunResponse execute(UUID workspaceId) {
        authorization.requireReminderRun(workspaceId);
        return ProjectReminderRunResponse.from(reminderService.runDaily(workspaceId));
    }
}
