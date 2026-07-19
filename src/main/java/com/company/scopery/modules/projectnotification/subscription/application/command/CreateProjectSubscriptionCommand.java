package com.company.scopery.modules.projectnotification.subscription.application.command;

import java.util.UUID;

public record CreateProjectSubscriptionCommand(
        UUID projectId, UUID subscriberUserId, String subscriptionType
) {}
