package com.company.scopery.modules.projectnotification.tasksubscription.application.command;

import java.util.UUID;

public record CreateTaskSubscriptionCommand(
        UUID projectId, UUID taskId, UUID subscriberUserId, String subscriptionType
) {}
