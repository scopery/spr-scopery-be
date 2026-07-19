package com.company.scopery.modules.projectnotification.preference.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectNotificationPreferenceJpaRepository
        extends JpaRepository<ProjectNotificationPreferenceJpaEntity, UUID> {
    List<ProjectNotificationPreferenceJpaEntity> findByProjectIdAndUserId(UUID projectId, UUID userId);
    List<ProjectNotificationPreferenceJpaEntity> findByTaskIdAndUserId(UUID taskId, UUID userId);
    Optional<ProjectNotificationPreferenceJpaEntity> findFirstByProjectIdAndTaskIdAndUserIdAndEventCodeAndChannel(
            UUID projectId, UUID taskId, UUID userId, String eventCode, String channel);
}
