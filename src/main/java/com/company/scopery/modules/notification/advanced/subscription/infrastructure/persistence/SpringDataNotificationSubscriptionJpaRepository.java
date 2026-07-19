package com.company.scopery.modules.notification.advanced.subscription.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataNotificationSubscriptionJpaRepository extends JpaRepository<NotificationSubscriptionJpaEntity, UUID> {
    List<NotificationSubscriptionJpaEntity> findByWorkspaceIdAndUserIdAndStatus(UUID workspaceId, UUID userId, String status);
}
