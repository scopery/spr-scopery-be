package com.company.scopery.modules.iam.permission.infrastructure.mapper;

import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.permission.infrastructure.persistence.IamPermissionActionDefinitionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamPermissionActionDefinitionPersistenceMapper {

    public IamPermissionActionDefinition toDomain(IamPermissionActionDefinitionJpaEntity entity) {
        return new IamPermissionActionDefinition(
                entity.getId(),
                entity.getPermissionId(),
                entity.getActionCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getRightId(),
                IamPermissionStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public IamPermissionActionDefinitionJpaEntity toJpaEntity(IamPermissionActionDefinition domain) {
        IamPermissionActionDefinitionJpaEntity entity = new IamPermissionActionDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setPermissionId(domain.permissionId());
        entity.setActionCode(domain.actionCode());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setRightId(domain.rightId());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
