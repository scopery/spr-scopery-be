package com.company.scopery.modules.integrationhub.credential.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateCredentialReferenceRequest(
        @NotBlank String providerCode,
        @NotBlank String credentialType,
        String secretReference) {}
