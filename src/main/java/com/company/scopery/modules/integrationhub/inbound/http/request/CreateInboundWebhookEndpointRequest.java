package com.company.scopery.modules.integrationhub.inbound.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateInboundWebhookEndpointRequest(
        @NotNull UUID connectionId,
        @NotBlank String endpointCode,
        @NotBlank String providerCode) {}
