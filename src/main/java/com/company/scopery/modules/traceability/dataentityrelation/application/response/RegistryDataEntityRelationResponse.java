package com.company.scopery.modules.traceability.dataentityrelation.application.response;

import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelation;

import java.time.Instant;
import java.util.UUID;

public record RegistryDataEntityRelationResponse(
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

    public static RegistryDataEntityRelationResponse from(RegistryDataEntityRelation d) {
        return new RegistryDataEntityRelationResponse(
                d.id(), d.sourceEntityId(), d.targetEntityId(), d.workspaceId(),
                d.relationType(), d.sourceColumn(), d.label(), d.note(),
                d.status(), d.version(), d.createdAt(), d.updatedAt());
    }
}
