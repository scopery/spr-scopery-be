package com.company.scopery.modules.projectnotification.tasksubscription.domain.model;

import com.company.scopery.modules.projectnotification.subscription.domain.enums.SubscriptionStatus;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.enums.TaskSubscriptionType;

import java.time.Instant;
import java.util.UUID;

public record TaskNotificationSubscription(
        UUID id,
        UUID projectId,
        UUID taskId,
        UUID workspaceId,
        UUID subscriberUserId,
        UUID workspaceMemberId,
        TaskSubscriptionType subscriptionType,
        SubscriptionStatus status,
        boolean mandatory,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskNotificationSubscription create(
            UUID projectId, UUID taskId, UUID workspaceId, UUID subscriberUserId, UUID workspaceMemberId,
            TaskSubscriptionType type, boolean mandatory) {
        Instant now = Instant.now();
        return new TaskNotificationSubscription(
                UUID.randomUUID(), projectId, taskId, workspaceId, subscriberUserId, workspaceMemberId,
                type, SubscriptionStatus.ACTIVE, mandatory, null, null, 0, now, now);
    }

    public TaskNotificationSubscription mute() {
        return withStatus(SubscriptionStatus.MUTED);
    }

    public TaskNotificationSubscription unmute() {
        return withStatus(SubscriptionStatus.ACTIVE);
    }

    public TaskNotificationSubscription archive(UUID actorId) {
        Instant now = Instant.now();
        return new TaskNotificationSubscription(
                id, projectId, taskId, workspaceId, subscriberUserId, workspaceMemberId, subscriptionType,
                SubscriptionStatus.ARCHIVED, mandatory, now, actorId, version, createdAt, now);
    }

    private TaskNotificationSubscription withStatus(SubscriptionStatus newStatus) {
        return new TaskNotificationSubscription(
                id, projectId, taskId, workspaceId, subscriberUserId, workspaceMemberId, subscriptionType,
                newStatus, mandatory, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
}
