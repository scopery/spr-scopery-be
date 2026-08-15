package com.company.scopery.modules.traceability.dataentityrelation.infrastructure.mapper;

import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelation;
import com.company.scopery.modules.traceability.dataentityrelation.infrastructure.persistence.RegistryDataEntityRelationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryDataEntityRelationPersistenceMapper {

    public RegistryDataEntityRelation toDomain(RegistryDataEntityRelationJpaEntity e) {
        return new RegistryDataEntityRelation(
                e.getId(), e.getSourceEntityId(), e.getTargetEntityId(), e.getWorkspaceId(),
                e.getRelationType(), e.getSourceColumn(), e.getLabel(), e.getNote(),
                e.getStatus(), e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public RegistryDataEntityRelationJpaEntity toJpaEntity(RegistryDataEntityRelation d) {
        RegistryDataEntityRelationJpaEntity e = new RegistryDataEntityRelationJpaEntity();
        e.setId(d.id());
        e.setSourceEntityId(d.sourceEntityId());
        e.setTargetEntityId(d.targetEntityId());
        e.setWorkspaceId(d.workspaceId());
        e.setRelationType(d.relationType());
        e.setSourceColumn(d.sourceColumn());
        e.setLabel(d.label());
        e.setNote(d.note());
        e.setStatus(d.status());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
