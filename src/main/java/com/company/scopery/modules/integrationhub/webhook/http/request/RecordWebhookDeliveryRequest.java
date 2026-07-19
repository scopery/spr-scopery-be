package com.company.scopery.modules.integrationhub.webhook.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record RecordWebhookDeliveryRequest(
        @NotNull UUID webhookSubscriptionId,
        String eventType,
        Integer attemptNumber,
        Boolean success,
        String responseBody) {}
