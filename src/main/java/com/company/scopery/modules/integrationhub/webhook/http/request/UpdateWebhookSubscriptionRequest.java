package com.company.scopery.modules.integrationhub.webhook.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateWebhookSubscriptionRequest(
        @NotBlank String name,
        @NotBlank String endpointUrl,
        @NotBlank String eventTypesJson,
        @NotBlank String payloadVersion) {}
