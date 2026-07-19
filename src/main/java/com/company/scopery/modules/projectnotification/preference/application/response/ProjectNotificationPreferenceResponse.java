package com.company.scopery.modules.projectnotification.preference.application.response;

import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreference;

import java.util.UUID;

public record ProjectNotificationPreferenceResponse(
        UUID id, UUID projectId, UUID taskId, UUID workspaceId, UUID userId,
        String eventCode, String channel, boolean enabled, boolean muted
) {
    public static ProjectNotificationPreferenceResponse from(ProjectNotificationPreference p) {
        return new ProjectNotificationPreferenceResponse(
                p.id(), p.projectId(), p.taskId(), p.workspaceId(), p.userId(),
                p.eventCode(), p.channel().name(), p.enabled(), p.muted());
    }
}
