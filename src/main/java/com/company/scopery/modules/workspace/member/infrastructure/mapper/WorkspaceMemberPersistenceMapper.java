package com.company.scopery.modules.workspace.member.infrastructure.mapper;

import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.infrastructure.persistence.WorkspaceMemberJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMemberPersistenceMapper {

    public WorkspaceMember toDomain(WorkspaceMemberJpaEntity entity) {
        return new WorkspaceMember(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getUserId(),
                WorkspaceMemberStatus.valueOf(entity.getStatus()),
                entity.getJoinedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public WorkspaceMemberJpaEntity toJpaEntity(WorkspaceMember domain) {
        WorkspaceMemberJpaEntity entity = new WorkspaceMemberJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setUserId(domain.userId());
        entity.setStatus(domain.status().name());
        entity.setJoinedAt(domain.joinedAt());
        entity.setCreatedAt(domain.createdAt()); // required for Spring Data merge()
        return entity;
    }
}
