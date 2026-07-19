package com.company.scopery.modules.projectnotification.subscription.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateProjectSubscriptionRequest(
        UUID subscriberUserId,
        @NotBlank String subscriptionType
) {}
