package com.company.scopery.modules.integrationhub.mapping.application.response;
import com.company.scopery.modules.integrationhub.mapping.domain.model.ExternalIdMapping;
import java.time.Instant; import java.util.UUID;
public record ExternalIdMappingResponse(UUID id, UUID connectionId, String providerCode, String externalObjectType,
        String externalId, String scoperyObjectType, UUID scoperyObjectId, Instant lastSyncedAt, String syncState) {
    public static ExternalIdMappingResponse from(ExternalIdMapping m) {
        return new ExternalIdMappingResponse(m.id(), m.connectionId(), m.providerCode(), m.externalObjectType(),
                m.externalId(), m.scoperyObjectType(), m.scoperyObjectId(), m.lastSyncedAt(), m.syncState());
    }
}
