package com.company.scopery.modules.notification.advanced.suppression.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataNotificationSuppressionJpaRepository extends JpaRepository<NotificationSuppressionJpaEntity, UUID> {
    List<NotificationSuppressionJpaEntity> findByWorkspaceIdOrderBySuppressedAtDesc(UUID workspaceId);
}
