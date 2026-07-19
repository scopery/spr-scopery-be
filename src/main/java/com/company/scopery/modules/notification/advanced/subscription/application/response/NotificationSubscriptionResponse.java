package com.company.scopery.modules.notification.advanced.subscription.application.response;
import com.company.scopery.modules.notification.advanced.subscription.domain.model.NotificationSubscription;
import java.util.UUID;
public record NotificationSubscriptionResponse(UUID id, String targetType, UUID targetId, String subscriptionLevel, String status) {
    public static NotificationSubscriptionResponse from(NotificationSubscription s) {
        return new NotificationSubscriptionResponse(s.id(), s.targetType(), s.targetId(), s.subscriptionLevel(), s.status());
    }
}
