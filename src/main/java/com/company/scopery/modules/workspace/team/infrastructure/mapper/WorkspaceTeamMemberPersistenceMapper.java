package com.company.scopery.modules.workspace.team.infrastructure.mapper;

import com.company.scopery.modules.workspace.team.domain.WorkspaceTeamMember;
import com.company.scopery.modules.workspace.team.infrastructure.persistence.WorkspaceTeamMemberJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceTeamMemberPersistenceMapper {

    public WorkspaceTeamMember toDomain(WorkspaceTeamMemberJpaEntity entity) {
        return new WorkspaceTeamMember(
                entity.getTeamId(),
                entity.getUserId(),
                entity.getCreatedAt());
    }

    public WorkspaceTeamMemberJpaEntity toJpaEntity(WorkspaceTeamMember domain) {
        WorkspaceTeamMemberJpaEntity entity = new WorkspaceTeamMemberJpaEntity();
        entity.setTeamId(domain.teamId());
        entity.setUserId(domain.userId());
        entity.setCreatedAt(domain.createdAt()); // required for Spring Data merge()
        return entity;
    }
}
