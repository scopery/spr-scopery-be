package com.company.scopery.modules.projectnotification.tasksubscription.domain.model;

import com.company.scopery.modules.projectnotification.tasksubscription.domain.enums.TaskSubscriptionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskNotificationSubscriptionRepository {
    TaskNotificationSubscription save(TaskNotificationSubscription subscription);
    Optional<TaskNotificationSubscription> findById(UUID id);
    List<TaskNotificationSubscription> findByTaskId(UUID taskId);
    List<TaskNotificationSubscription> findByTaskIdAndSubscriberUserId(UUID taskId, UUID userId);
    List<TaskNotificationSubscription> findActiveByTaskId(UUID taskId);
    boolean existsActive(UUID taskId, UUID subscriberUserId, TaskSubscriptionType type);
}
