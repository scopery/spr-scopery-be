package com.company.scopery.modules.projectnotification.subscription.application.response;

import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscription;

import java.time.Instant;
import java.util.UUID;

public record ProjectNotificationSubscriptionResponse(
        UUID id, UUID projectId, UUID workspaceId, UUID subscriberUserId, UUID workspaceMemberId,
        String subscriptionType, String status, boolean mandatory,
        Instant archivedAt, Instant createdAt, Instant updatedAt
) {
    public static ProjectNotificationSubscriptionResponse from(ProjectNotificationSubscription s) {
        return new ProjectNotificationSubscriptionResponse(
                s.id(), s.projectId(), s.workspaceId(), s.subscriberUserId(), s.workspaceMemberId(),
                s.subscriptionType().name(), s.status().name(), s.mandatory(),
                s.archivedAt(), s.createdAt(), s.updatedAt());
    }
}
