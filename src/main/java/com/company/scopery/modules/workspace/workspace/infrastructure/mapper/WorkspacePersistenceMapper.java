package com.company.scopery.modules.workspace.workspace.infrastructure.mapper;

import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.infrastructure.persistence.WorkspaceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspacePersistenceMapper {

    public Workspace toDomain(WorkspaceJpaEntity entity) {
        return new Workspace(
                entity.getId(),
                entity.getOrganizationId(),
                WorkspaceCode.of(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                entity.getOwnerUserId(),
                WorkspaceVisibility.valueOf(entity.getDefaultVisibility()),
                WorkspaceStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public WorkspaceJpaEntity toJpaEntity(Workspace domain) {
        WorkspaceJpaEntity entity = new WorkspaceJpaEntity();
        entity.setId(domain.id());
        entity.setOrganizationId(domain.organizationId());
        entity.setCode(domain.code().value());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setOwnerUserId(domain.ownerUserId());
        entity.setDefaultVisibility(domain.defaultVisibility().name());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt()); // required for Spring Data merge()
        return entity;
    }
}
