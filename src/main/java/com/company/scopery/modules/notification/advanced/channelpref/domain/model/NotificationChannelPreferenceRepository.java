package com.company.scopery.modules.notification.advanced.channelpref.domain.model;
import java.util.*; import java.util.UUID;
public interface NotificationChannelPreferenceRepository {
    NotificationChannelPreference save(NotificationChannelPreference p);
    List<NotificationChannelPreference> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
    Optional<NotificationChannelPreference> findOne(UUID workspaceId, UUID userId, String category, String channel);
}
