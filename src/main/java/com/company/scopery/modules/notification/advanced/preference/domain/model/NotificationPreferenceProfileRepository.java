package com.company.scopery.modules.notification.advanced.preference.domain.model;
import java.util.Optional; import java.util.UUID;
public interface NotificationPreferenceProfileRepository {
    NotificationPreferenceProfile save(NotificationPreferenceProfile p);
    Optional<NotificationPreferenceProfile> findByWorkspaceAndUser(UUID workspaceId, UUID userId);
}
