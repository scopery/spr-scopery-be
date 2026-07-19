package com.company.scopery.modules.projectnotification.subscription.domain.model;

import com.company.scopery.modules.projectnotification.subscription.domain.enums.ProjectSubscriptionType;
import com.company.scopery.modules.projectnotification.subscription.domain.enums.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record ProjectNotificationSubscription(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID subscriberUserId,
        UUID workspaceMemberId,
        ProjectSubscriptionType subscriptionType,
        SubscriptionStatus status,
        boolean mandatory,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectNotificationSubscription create(
            UUID projectId, UUID workspaceId, UUID subscriberUserId, UUID workspaceMemberId,
            ProjectSubscriptionType type, boolean mandatory) {
        Instant now = Instant.now();
        return new ProjectNotificationSubscription(
                UUID.randomUUID(), projectId, workspaceId, subscriberUserId, workspaceMemberId,
                type, SubscriptionStatus.ACTIVE, mandatory, null, null, 0, now, now);
    }

    public ProjectNotificationSubscription mute() {
        if (status == SubscriptionStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot mute archived subscription");
        }
        return withStatus(SubscriptionStatus.MUTED);
    }

    public ProjectNotificationSubscription unmute() {
        if (status == SubscriptionStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot unmute archived subscription");
        }
        return withStatus(SubscriptionStatus.ACTIVE);
    }

    public ProjectNotificationSubscription archive(UUID actorId) {
        Instant now = Instant.now();
        return new ProjectNotificationSubscription(
                id, projectId, workspaceId, subscriberUserId, workspaceMemberId, subscriptionType,
                SubscriptionStatus.ARCHIVED, mandatory, now, actorId, version, createdAt, now);
    }

    private ProjectNotificationSubscription withStatus(SubscriptionStatus newStatus) {
        return new ProjectNotificationSubscription(
                id, projectId, workspaceId, subscriberUserId, workspaceMemberId, subscriptionType,
                newStatus, mandatory, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
}
