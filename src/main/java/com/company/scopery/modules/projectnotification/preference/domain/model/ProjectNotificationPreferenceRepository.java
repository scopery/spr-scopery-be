package com.company.scopery.modules.projectnotification.preference.domain.model;

import com.company.scopery.modules.projectnotification.preference.domain.enums.PreferenceChannel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectNotificationPreferenceRepository {
    ProjectNotificationPreference save(ProjectNotificationPreference preference);
    List<ProjectNotificationPreference> findByProjectIdAndUserId(UUID projectId, UUID userId);
    List<ProjectNotificationPreference> findByTaskIdAndUserId(UUID taskId, UUID userId);
    Optional<ProjectNotificationPreference> findMatching(
            UUID projectId, UUID taskId, UUID userId, String eventCode, PreferenceChannel channel);
}
