package com.company.scopery.modules.integrationhub.provider.application.response;
import com.company.scopery.modules.integrationhub.provider.domain.model.IntegrationProvider;
import java.time.Instant; import java.util.UUID;
public record IntegrationProviderResponse(UUID id, String providerCode, String name, String category,
        String description, String adapterKey, boolean enabled, boolean seedOnly,
        Instant createdAt, Instant updatedAt) {
    public static IntegrationProviderResponse from(IntegrationProvider p) {
        return new IntegrationProviderResponse(p.id(), p.providerCode(), p.name(), p.category(),
                p.description(), p.adapterKey(), p.enabled(), p.seedOnly(), p.createdAt(), p.updatedAt());
    }
}
