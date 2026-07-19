package com.company.scopery.modules.projectnotification.tasksubscription.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateTaskSubscriptionRequest(
        UUID subscriberUserId,
        @NotBlank String subscriptionType
) {}
