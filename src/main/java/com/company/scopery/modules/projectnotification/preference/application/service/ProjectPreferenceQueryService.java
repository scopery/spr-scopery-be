package com.company.scopery.modules.projectnotification.preference.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.projectnotification.preference.application.response.ProjectNotificationPreferenceResponse;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreferenceRepository;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectPreferenceQueryService {
    private final ProjectNotificationPreferenceRepository preferences;
    private final ProjectNotificationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;

    public ProjectPreferenceQueryService(ProjectNotificationPreferenceRepository preferences,
                                         ProjectNotificationAuthorizationService authorization,
                                         CurrentUserAuthorizationService currentUser) {
        this.preferences = preferences;
        this.authorization = authorization;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public List<ProjectNotificationPreferenceResponse> listMine(UUID projectId) {
        authorization.requirePreferenceView(projectId);
        UUID userId = currentUser.resolveCurrentUser().id();
        return preferences.findByProjectIdAndUserId(projectId, userId).stream()
                .map(ProjectNotificationPreferenceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectNotificationPreferenceResponse> listMineForTask(UUID projectId, UUID taskId) {
        authorization.requirePreferenceView(projectId);
        UUID userId = currentUser.resolveCurrentUser().id();
        return preferences.findByTaskIdAndUserId(taskId, userId).stream()
                .map(ProjectNotificationPreferenceResponse::from).toList();
    }
}
