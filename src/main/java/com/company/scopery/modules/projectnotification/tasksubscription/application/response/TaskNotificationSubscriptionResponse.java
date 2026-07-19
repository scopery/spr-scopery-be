package com.company.scopery.modules.projectnotification.tasksubscription.application.response;

import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscription;

import java.time.Instant;
import java.util.UUID;

public record TaskNotificationSubscriptionResponse(
        UUID id, UUID projectId, UUID taskId, UUID workspaceId, UUID subscriberUserId, UUID workspaceMemberId,
        String subscriptionType, String status, boolean mandatory,
        Instant archivedAt, Instant createdAt, Instant updatedAt
) {
    public static TaskNotificationSubscriptionResponse from(TaskNotificationSubscription s) {
        return new TaskNotificationSubscriptionResponse(
                s.id(), s.projectId(), s.taskId(), s.workspaceId(), s.subscriberUserId(), s.workspaceMemberId(),
                s.subscriptionType().name(), s.status().name(), s.mandatory(),
                s.archivedAt(), s.createdAt(), s.updatedAt());
    }
}
