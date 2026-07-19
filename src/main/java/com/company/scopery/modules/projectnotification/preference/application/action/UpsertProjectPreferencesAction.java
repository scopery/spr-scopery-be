package com.company.scopery.modules.projectnotification.preference.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectnotification.preference.application.command.UpsertProjectPreferencesCommand;
import com.company.scopery.modules.projectnotification.preference.application.response.ProjectNotificationPreferenceResponse;
import com.company.scopery.modules.projectnotification.preference.domain.enums.PreferenceChannel;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreference;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreferenceRepository;
import com.company.scopery.modules.projectnotification.shared.activity.ProjectNotificationActivityLogger;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationActivityActions;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEntityTypes;
import com.company.scopery.modules.projectnotification.shared.error.ProjectNotificationExceptions;
import com.company.scopery.modules.projectnotification.shared.util.ProjectNotificationEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UpsertProjectPreferencesAction {
    private final ProjectRepository projects;
    private final ProjectNotificationPreferenceRepository preferences;
    private final ProjectNotificationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectNotificationActivityLogger activityLogger;

    public UpsertProjectPreferencesAction(
            ProjectRepository projects,
            ProjectNotificationPreferenceRepository preferences,
            ProjectNotificationAuthorizationService authorization,
            CurrentUserAuthorizationService currentUser,
            ProjectNotificationActivityLogger activityLogger) {
        this.projects = projects;
        this.preferences = preferences;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public List<ProjectNotificationPreferenceResponse> execute(UpsertProjectPreferencesCommand cmd) {
        authorization.requirePreferenceUpdate(cmd.projectId());
        Project project = projects.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));
        UUID userId = currentUser.resolveCurrentUser().id();
        List<ProjectNotificationPreferenceResponse> saved = new ArrayList<>();
        for (var item : cmd.preferences()) {
            PreferenceChannel channel = ProjectNotificationEnumParser.parseRequired(
                    PreferenceChannel.class, item.channel(),
                    "PROJECT_NOTIFICATION_PREFERENCE_INVALID_CHANNEL", "channel");
            if (channel != PreferenceChannel.EMAIL && channel != PreferenceChannel.IN_APP) {
                throw ProjectNotificationExceptions.preferenceInvalidChannel();
            }
            var existing = preferences.findMatching(
                    cmd.projectId(), cmd.taskId(), userId, item.eventCode(), channel);
            ProjectNotificationPreference pref = ProjectNotificationPreference.upsert(
                    existing.map(ProjectNotificationPreference::id).orElse(null),
                    cmd.projectId(), cmd.taskId(), project.workspaceId(), userId,
                    item.eventCode(), channel, item.enabled(), item.muted());
            pref = preferences.save(pref);
            saved.add(ProjectNotificationPreferenceResponse.from(pref));
            activityLogger.logSuccess(ProjectNotificationEntityTypes.PREFERENCE, pref.id(),
                    ProjectNotificationActivityActions.UPDATE_PREFERENCE, "Updated preference " + channel);
        }
        return saved;
    }
}
