package com.company.scopery.modules.aiagent.providersecret.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RotateProviderSecretRequest(
        @NotBlank @Size(max = 5000) String secretValue,
        @Size(max = 500) String description
) {}
