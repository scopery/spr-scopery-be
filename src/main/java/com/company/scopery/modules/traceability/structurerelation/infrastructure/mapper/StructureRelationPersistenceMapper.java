package com.company.scopery.modules.traceability.structurerelation.infrastructure.mapper;

import com.company.scopery.modules.traceability.structurerelation.domain.enums.StructureRelationNodeType;
import com.company.scopery.modules.traceability.structurerelation.domain.enums.StructureRelationType;
import com.company.scopery.modules.traceability.structurerelation.domain.model.StructureRelation;
import com.company.scopery.modules.traceability.structurerelation.infrastructure.persistence.StructureRelationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class StructureRelationPersistenceMapper {

    public StructureRelation toDomain(StructureRelationJpaEntity e) {
        return new StructureRelation(
                e.getId(),
                e.getApplicationId(),
                e.getWorkspaceId(),
                StructureRelationNodeType.valueOf(e.getFromNodeType()),
                e.getFromNodeId(),
                StructureRelationNodeType.valueOf(e.getToNodeType()),
                e.getToNodeId(),
                StructureRelationType.valueOf(e.getRelationType()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public StructureRelationJpaEntity toJpaEntity(StructureRelation d) {
        StructureRelationJpaEntity e = new StructureRelationJpaEntity();
        e.setId(d.id());
        e.setApplicationId(d.applicationId());
        e.setWorkspaceId(d.workspaceId());
        e.setFromNodeType(d.fromNodeType().name());
        e.setFromNodeId(d.fromNodeId());
        e.setToNodeType(d.toNodeType().name());
        e.setToNodeId(d.toNodeId());
        e.setRelationType(d.relationType().name());
        // New: leave version/createdAt null → persist. Update: stamp both for optimistic lock.
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
