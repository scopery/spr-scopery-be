package com.company.scopery.modules.projectnotification.subscription.domain.model;

import com.company.scopery.modules.projectnotification.subscription.domain.enums.ProjectSubscriptionType;
import com.company.scopery.modules.projectnotification.subscription.domain.enums.SubscriptionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectNotificationSubscriptionRepository {
    ProjectNotificationSubscription save(ProjectNotificationSubscription subscription);
    Optional<ProjectNotificationSubscription> findById(UUID id);
    List<ProjectNotificationSubscription> findByProjectId(UUID projectId);
    List<ProjectNotificationSubscription> findByProjectIdAndSubscriberUserId(UUID projectId, UUID userId);
    List<ProjectNotificationSubscription> findActiveByProjectIdAndType(UUID projectId, ProjectSubscriptionType type);
    boolean existsActive(UUID projectId, UUID subscriberUserId, ProjectSubscriptionType type);
    List<ProjectNotificationSubscription> findByProjectIdAndStatus(UUID projectId, SubscriptionStatus status);
}
