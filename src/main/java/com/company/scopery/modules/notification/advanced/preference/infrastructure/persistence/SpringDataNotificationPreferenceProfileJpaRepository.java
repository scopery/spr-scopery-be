package com.company.scopery.modules.notification.advanced.preference.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface SpringDataNotificationPreferenceProfileJpaRepository extends JpaRepository<NotificationPreferenceProfileJpaEntity, UUID> {
    Optional<NotificationPreferenceProfileJpaEntity> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
