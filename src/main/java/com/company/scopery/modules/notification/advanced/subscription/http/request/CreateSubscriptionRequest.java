package com.company.scopery.modules.notification.advanced.subscription.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateSubscriptionRequest(@NotBlank String targetType, @NotNull UUID targetId, @NotBlank String subscriptionLevel) {}
