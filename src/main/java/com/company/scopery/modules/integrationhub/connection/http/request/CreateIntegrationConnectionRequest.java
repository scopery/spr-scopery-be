package com.company.scopery.modules.integrationhub.connection.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateIntegrationConnectionRequest(
        @NotBlank String providerCode,
        @NotBlank String name,
        UUID credentialReferenceId) {}
