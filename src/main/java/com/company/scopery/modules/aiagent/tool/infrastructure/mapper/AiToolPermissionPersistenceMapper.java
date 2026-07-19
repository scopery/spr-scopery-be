package com.company.scopery.modules.aiagent.tool.infrastructure.mapper;

import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermission;
import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiToolPermissionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiToolPermissionPersistenceMapper {

    public AiToolPermissionJpaEntity toJpaEntity(AiToolPermission permission) {
        AiToolPermissionJpaEntity entity = new AiToolPermissionJpaEntity();
        entity.setId(permission.id());
        entity.setToolId(permission.toolId());
        entity.setPermissionCode(permission.permissionCode());
        entity.setDescription(permission.description());
        if (permission.createdAt() != null) {
            entity.setCreatedAt(permission.createdAt());
        }
        return entity;
    }

    public AiToolPermission toDomain(AiToolPermissionJpaEntity entity) {
        return AiToolPermission.reconstitute(
                entity.getId(),
                entity.getToolId(),
                entity.getPermissionCode(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
