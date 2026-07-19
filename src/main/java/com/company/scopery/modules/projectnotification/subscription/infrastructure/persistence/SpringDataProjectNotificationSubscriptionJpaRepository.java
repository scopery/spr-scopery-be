package com.company.scopery.modules.projectnotification.subscription.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectNotificationSubscriptionJpaRepository
        extends JpaRepository<ProjectNotificationSubscriptionJpaEntity, UUID> {
    List<ProjectNotificationSubscriptionJpaEntity> findByProjectId(UUID projectId);
    List<ProjectNotificationSubscriptionJpaEntity> findByProjectIdAndSubscriberUserId(UUID projectId, UUID userId);
    List<ProjectNotificationSubscriptionJpaEntity> findByProjectIdAndSubscriptionTypeAndStatus(
            UUID projectId, String subscriptionType, String status);
    List<ProjectNotificationSubscriptionJpaEntity> findByProjectIdAndStatus(UUID projectId, String status);
    boolean existsByProjectIdAndSubscriberUserIdAndSubscriptionTypeAndStatus(
            UUID projectId, UUID subscriberUserId, String subscriptionType, String status);
}
