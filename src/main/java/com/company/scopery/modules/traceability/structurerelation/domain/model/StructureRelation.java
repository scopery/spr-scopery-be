package com.company.scopery.modules.traceability.structurerelation.domain.model;

import com.company.scopery.modules.traceability.structurerelation.domain.enums.StructureRelationNodeType;
import com.company.scopery.modules.traceability.structurerelation.domain.enums.StructureRelationType;

import java.time.Instant;
import java.util.UUID;

public record StructureRelation(
        UUID id,
        UUID applicationId,
        UUID workspaceId,
        StructureRelationNodeType fromNodeType,
        UUID fromNodeId,
        StructureRelationNodeType toNodeType,
        UUID toNodeId,
        StructureRelationType relationType,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static StructureRelation create(
            UUID applicationId, UUID workspaceId,
            StructureRelationNodeType fromNodeType, UUID fromNodeId,
            StructureRelationNodeType toNodeType, UUID toNodeId,
            StructureRelationType relationType) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new StructureRelation(UUID.randomUUID(), applicationId, workspaceId,
                fromNodeType, fromNodeId, toNodeType, toNodeId, relationType, 0, null, null);
    }
}
