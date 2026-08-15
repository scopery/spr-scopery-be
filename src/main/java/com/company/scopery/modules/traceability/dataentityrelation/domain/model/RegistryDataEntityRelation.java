package com.company.scopery.modules.traceability.dataentityrelation.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RegistryDataEntityRelation(
        UUID id,
        UUID sourceEntityId,
        UUID targetEntityId,
        UUID workspaceId,
        String relationType,
        String sourceColumn,
        String label,
        String note,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistryDataEntityRelation create(UUID sourceEntityId, UUID targetEntityId,
                                                    UUID workspaceId, String relationType,
                                                    String sourceColumn, String label, String note) {
        return new RegistryDataEntityRelation(
                UUID.randomUUID(), sourceEntityId, targetEntityId, workspaceId,
                relationType, sourceColumn, label, note,
                "ACTIVE", 0, null, null);
    }

    public RegistryDataEntityRelation withUpdated(String relationType, String sourceColumn,
                                                   String label, String note) {
        return new RegistryDataEntityRelation(
                id, sourceEntityId, targetEntityId, workspaceId,
                relationType, sourceColumn, label, note,
                status, version, createdAt, Instant.now());
    }
}
