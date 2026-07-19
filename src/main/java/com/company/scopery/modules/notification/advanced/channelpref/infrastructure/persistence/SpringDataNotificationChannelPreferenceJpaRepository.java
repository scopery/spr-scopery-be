package com.company.scopery.modules.notification.advanced.channelpref.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataNotificationChannelPreferenceJpaRepository extends JpaRepository<NotificationChannelPreferenceJpaEntity, UUID> {
    List<NotificationChannelPreferenceJpaEntity> findByWorkspaceIdAndUserIdOrderByCategoryCodeAscChannelCodeAsc(UUID workspaceId, UUID userId);
    Optional<NotificationChannelPreferenceJpaEntity> findByWorkspaceIdAndUserIdAndCategoryCodeAndChannelCode(UUID workspaceId, UUID userId, String categoryCode, String channelCode);
}
