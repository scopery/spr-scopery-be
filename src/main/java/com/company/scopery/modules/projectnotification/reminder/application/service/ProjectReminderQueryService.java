package com.company.scopery.modules.projectnotification.reminder.application.service;

import com.company.scopery.modules.projectnotification.reminder.application.response.ProjectReminderRunResponse;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderRunRepository;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.error.ProjectNotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectReminderQueryService {
    private final ProjectReminderRunRepository runs;
    private final ProjectNotificationAuthorizationService authorization;

    public ProjectReminderQueryService(ProjectReminderRunRepository runs,
                                       ProjectNotificationAuthorizationService authorization) {
        this.runs = runs;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProjectReminderRunResponse> list(UUID workspaceId) {
        authorization.requireReminderRun(workspaceId);
        return runs.findRecent(50).stream().map(ProjectReminderRunResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProjectReminderRunResponse get(UUID workspaceId, UUID runId) {
        authorization.requireReminderRun(workspaceId);
        return runs.findById(runId).map(ProjectReminderRunResponse::from)
                .orElseThrow(() -> ProjectNotificationExceptions.reminderRunNotFound(runId));
    }
}
