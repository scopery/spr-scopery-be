package com.company.scopery.modules.iam.resource.infrastructure.mapper;

import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.IamResourceVisibility;
import com.company.scopery.modules.iam.resource.infrastructure.persistence.IamAuthResourceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamAuthResourcePersistenceMapper {

    public IamAuthResource toDomain(IamAuthResourceJpaEntity entity) {
        return new IamAuthResource(
                entity.getId(),
                IamResourceCode.of(entity.getCode()),
                IamResourceType.valueOf(entity.getResourceType()),
                entity.getName(),
                entity.getDescription(),
                entity.getRefId(),
                entity.getOwnerUserId(),
                entity.getWorkspaceId(),
                entity.getVisibility() != null ? IamResourceVisibility.valueOf(entity.getVisibility()) : null,
                entity.getParentResourceId(),
                IamResourceStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public IamAuthResourceJpaEntity toJpaEntity(IamAuthResource domain) {
        IamAuthResourceJpaEntity entity = new IamAuthResourceJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code().value());
        entity.setResourceType(domain.resourceType().name());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setRefId(domain.refId());
        entity.setOwnerUserId(domain.ownerUserId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setVisibility(domain.visibility() != null ? domain.visibility().name() : null);
        entity.setParentResourceId(domain.parentResourceId());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
