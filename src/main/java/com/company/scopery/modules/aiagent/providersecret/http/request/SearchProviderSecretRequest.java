package com.company.scopery.modules.aiagent.providersecret.http.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SearchProviderSecretRequest(
        UUID providerId,
        @Size(max = 50) String secretType,
        @Size(max = 50) String status,
        @Min(0) int page,
        @Min(1) @Max(100) int size
) {}
