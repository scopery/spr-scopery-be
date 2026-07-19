package com.company.scopery.modules.notification.advanced.suppression.domain.model;
import java.util.List; import java.util.UUID;
public interface NotificationSuppressionRepository {
    NotificationSuppressionEntry save(NotificationSuppressionEntry e);
    List<NotificationSuppressionEntry> findByWorkspaceId(UUID workspaceId);
}
