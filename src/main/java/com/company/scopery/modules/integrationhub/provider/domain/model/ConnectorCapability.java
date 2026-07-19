package com.company.scopery.modules.integrationhub.provider.domain.model;
import java.time.Instant; import java.util.UUID;
public record ConnectorCapability(UUID id, String providerCode, String capabilityCode, String direction,
        boolean enabled, String description, int version, Instant createdAt, Instant updatedAt) {

    public static ConnectorCapability create(String providerCode, String capabilityCode, String direction) {
        Instant now = Instant.now();
        return new ConnectorCapability(UUID.randomUUID(), providerCode, capabilityCode, direction, true, null, 0, now, now);
    }
}
