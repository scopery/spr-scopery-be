package com.company.scopery.modules.traceability.structurerelation.application.response;

import com.company.scopery.modules.traceability.structurerelation.domain.model.StructureRelation;

import java.time.Instant;
import java.util.UUID;

public record StructureRelationResponse(
        UUID id,
        UUID applicationId,
        UUID workspaceId,
        String fromNodeType,
        UUID fromNodeId,
        String toNodeType,
        UUID toNodeId,
        String relationType,
        Instant createdAt,
        Instant updatedAt) {

    public static StructureRelationResponse from(StructureRelation d) {
        return new StructureRelationResponse(
                d.id(),
                d.applicationId(),
                d.workspaceId(),
                d.fromNodeType().name(),
                d.fromNodeId(),
                d.toNodeType().name(),
                d.toNodeId(),
                d.relationType().name(),
                d.createdAt(),
                d.updatedAt());
    }
}
