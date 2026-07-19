package com.company.scopery.modules.integrationhub.webhook.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateWebhookSubscriptionRequest(
        @NotBlank String name,
        @NotBlank String endpointUrl,
        @NotBlank String eventTypesJson,
        @NotBlank String payloadVersion,
        UUID connectionId) {}
