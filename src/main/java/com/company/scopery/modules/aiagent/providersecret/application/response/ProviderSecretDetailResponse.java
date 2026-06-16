package com.company.scopery.modules.aiagent.providersecret.application.response;

import com.company.scopery.modules.aiagent.providersecret.domain.ProviderSecret;

import java.time.Instant;
import java.util.UUID;

public record ProviderSecretDetailResponse(
        UUID id,
        UUID providerId,
        String secretType,
        String maskedValue,
        String keyVersion,
        String description,
        String status,
        Instant lastRotatedAt,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProviderSecretDetailResponse from(ProviderSecret secret) {
        return new ProviderSecretDetailResponse(
                secret.id(),
                secret.providerId(),
                secret.secretType().name(),
                secret.maskedValue(),
                secret.keyVersion(),
                secret.description(),
                secret.status().name(),
                secret.lastRotatedAt(),
                secret.createdAt(),
                secret.updatedAt()
        );
    }
}
