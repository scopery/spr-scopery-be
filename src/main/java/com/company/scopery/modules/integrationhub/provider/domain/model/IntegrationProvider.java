package com.company.scopery.modules.integrationhub.provider.domain.model;
import java.time.Instant; import java.util.UUID;
public record IntegrationProvider(UUID id, String providerCode, String name, String category, String description,
        String adapterKey, boolean enabled, boolean seedOnly, String capabilitiesJson,
        int version, Instant createdAt, Instant updatedAt) {

    public static IntegrationProvider seed(String code, String name, String category, boolean seedOnly) {
        Instant now = Instant.now();
        return new IntegrationProvider(UUID.randomUUID(), code, name, category, null, null,
                true, seedOnly, null, 0, now, now);
    }

    public IntegrationProvider disable() {
        return new IntegrationProvider(id, providerCode, name, category, description, adapterKey, false, seedOnly, capabilitiesJson, version, createdAt, Instant.now());
    }

    public IntegrationProvider enable() {
        return new IntegrationProvider(id, providerCode, name, category, description, adapterKey, true, seedOnly, capabilitiesJson, version, createdAt, Instant.now());
    }
}
