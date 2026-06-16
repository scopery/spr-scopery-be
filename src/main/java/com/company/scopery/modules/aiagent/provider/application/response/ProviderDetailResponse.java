package com.company.scopery.modules.aiagent.provider.application.response;

import com.company.scopery.modules.aiagent.provider.domain.Provider;

import java.time.Instant;
import java.util.UUID;

public record ProviderDetailResponse(
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

    public static ProviderDetailResponse from(Provider provider) {
        return new ProviderDetailResponse(
                provider.id(),
                provider.name(),
                provider.code().value(),
                provider.type(),
                provider.apiBaseUrl(),
                provider.description(),
                provider.status().name(),
                provider.createdAt(),
                provider.updatedAt()
        );
    }
}