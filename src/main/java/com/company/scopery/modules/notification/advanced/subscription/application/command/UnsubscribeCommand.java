package com.company.scopery.modules.notification.advanced.subscription.application.command;
import java.util.UUID;
public record UnsubscribeCommand(UUID workspaceId, UUID subscriptionId) {}
