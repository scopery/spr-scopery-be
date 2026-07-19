package com.company.scopery.modules.notification.advanced.subscription.application.command;
import java.util.UUID;
public record CreateSubscriptionCommand(UUID workspaceId, String targetType, UUID targetId, String subscriptionLevel) {}
