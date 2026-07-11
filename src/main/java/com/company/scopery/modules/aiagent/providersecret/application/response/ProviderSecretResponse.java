package com.company.scopery.modules.aiagent.providersecret.application.response;

import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecret;

import java.time.Instant;
import java.util.UUID;

public record ProviderSecretResponse(
        UUID id,
        UUID providerId,
        String secretType,
        String maskedValue,
        String description,
        String status,
        Instant lastRotatedAt,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProviderSecretResponse from(ProviderSecret secret) {
        return new ProviderSecretResponse(
                secret.id(),
                secret.providerId(),
                secret.secretType().name(),
                secret.maskedValue(),
                secret.description(),
                secret.status().name(),
                secret.lastRotatedAt(),
                secret.createdAt(),
                secret.updatedAt()
        );
    }
}
