package com.company.scopery.modules.notification.advanced.subscription.domain.model;
import java.util.*; import java.util.UUID;
public interface NotificationSubscriptionRepository {
    NotificationSubscription save(NotificationSubscription s);
    Optional<NotificationSubscription> findById(UUID id);
    List<NotificationSubscription> findActiveByUser(UUID workspaceId, UUID userId);
}
