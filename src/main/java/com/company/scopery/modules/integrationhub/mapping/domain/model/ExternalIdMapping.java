package com.company.scopery.modules.integrationhub.mapping.domain.model;
import java.time.Instant; import java.util.UUID;
public record ExternalIdMapping(UUID id, UUID workspaceId, UUID connectionId, String providerCode,
        String externalObjectType, String externalId, String scoperyObjectType, UUID scoperyObjectId,
        Instant lastSyncedAt, String syncState, int version, Instant createdAt, Instant updatedAt) {

    public static ExternalIdMapping create(UUID workspaceId, UUID connectionId, String providerCode,
            String externalObjectType, String externalId, String scoperyObjectType, UUID scoperyObjectId) {
        Instant now = Instant.now();
        return new ExternalIdMapping(UUID.randomUUID(), workspaceId, connectionId, providerCode,
                externalObjectType, externalId, scoperyObjectType, scoperyObjectId, null, "IN_SYNC", 0, now, now);
    }
}
