package com.company.scopery.modules.aiagent.provider.application.response;

import com.company.scopery.modules.aiagent.provider.domain.model.Provider;

import java.time.Instant;
import java.util.UUID;

public record ProviderResponse(
        UUID id,
        String name,
        String code,
        String type,
        String apiBaseUrl,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProviderResponse from(Provider provider) {
        return new ProviderResponse(
                provider.id(),
                provider.name(),
                provider.code().value(),
                provider.type().name(),
                provider.apiBaseUrl(),
                provider.description(),
                provider.status().name(),
                provider.createdAt(),
                provider.updatedAt()
        );
    }
}