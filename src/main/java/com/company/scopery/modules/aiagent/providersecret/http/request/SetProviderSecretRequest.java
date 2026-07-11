package com.company.scopery.modules.aiagent.providersecret.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SetProviderSecretRequest(
        @NotNull UUID providerId,
        @NotBlank @Size(max = 50) String secretType,
        @NotBlank @Size(max = 5000) String secretValue,
        @Size(max = 500) String description
) {}
