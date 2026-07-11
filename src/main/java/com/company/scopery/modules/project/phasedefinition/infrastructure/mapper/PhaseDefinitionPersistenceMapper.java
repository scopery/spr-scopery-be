package com.company.scopery.modules.project.phasedefinition.infrastructure.mapper;

import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;
import com.company.scopery.modules.project.phasedefinition.infrastructure.persistence.PhaseDefinitionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PhaseDefinitionPersistenceMapper {

    public PhaseDefinition toDomain(PhaseDefinitionJpaEntity entity) {
        return new PhaseDefinition(
                entity.getId(),
                entity.getScope() != null ? PhaseDefinitionScope.valueOf(entity.getScope()) : null,
                entity.getWorkspaceId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getDisplayOrder(),
                entity.isSystemDefault(),
                entity.getStatus() != null ? PhaseDefinitionStatus.valueOf(entity.getStatus()) : null,
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public PhaseDefinitionJpaEntity toJpaEntity(PhaseDefinition domain) {
        PhaseDefinitionJpaEntity entity = new PhaseDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setScope(domain.scope() != null ? domain.scope().name() : null);
        entity.setWorkspaceId(domain.workspaceId());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setDisplayOrder(domain.displayOrder());
        entity.setSystemDefault(domain.isSystemDefault());
        entity.setStatus(domain.status() != null ? domain.status().name() : null);
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
