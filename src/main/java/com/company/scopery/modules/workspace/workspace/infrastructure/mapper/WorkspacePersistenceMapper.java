package com.company.scopery.modules.workspace.workspace.infrastructure.mapper;

import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.infrastructure.persistence.WorkspaceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspacePersistenceMapper {

    public Workspace toDomain(WorkspaceJpaEntity entity) {
        WorkspaceJoinPolicy joinPolicy = entity.getJoinPolicy() != null
                ? WorkspaceJoinPolicy.valueOf(entity.getJoinPolicy())
                : WorkspaceJoinPolicy.INVITE_ONLY;
        return new Workspace(
                entity.getId(),
                entity.getOrganizationId(),
                WorkspaceCode.of(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                entity.getOwnerUserId(),
                WorkspaceVisibility.valueOf(entity.getDefaultVisibility()),
                joinPolicy,
                WorkspaceStatus.valueOf(entity.getStatus()),
                entity.getVersion(),
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
        entity.setJoinPolicy(domain.joinPolicy() != null ? domain.joinPolicy().name() : WorkspaceJoinPolicy.INVITE_ONLY.name());
        entity.setStatus(domain.status().name());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
