package com.company.scopery.modules.projectnotification.tasksubscription.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskNotificationSubscriptionJpaRepository
        extends JpaRepository<TaskNotificationSubscriptionJpaEntity, UUID> {
    List<TaskNotificationSubscriptionJpaEntity> findByTaskId(UUID taskId);
    List<TaskNotificationSubscriptionJpaEntity> findByTaskIdAndSubscriberUserId(UUID taskId, UUID userId);
    List<TaskNotificationSubscriptionJpaEntity> findByTaskIdAndStatus(UUID taskId, String status);
    boolean existsByTaskIdAndSubscriberUserIdAndSubscriptionTypeAndStatus(
            UUID taskId, UUID subscriberUserId, String subscriptionType, String status);
}
