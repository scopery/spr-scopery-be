package com.company.scopery.modules.workspace.team.infrastructure.mapper;

import com.company.scopery.modules.workspace.team.domain.valueobject.TeamCode;
import com.company.scopery.modules.workspace.team.domain.enums.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import com.company.scopery.modules.workspace.team.infrastructure.persistence.WorkspaceTeamJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceTeamPersistenceMapper {

    public WorkspaceTeam toDomain(WorkspaceTeamJpaEntity entity) {
        return new WorkspaceTeam(
                entity.getId(),
                entity.getWorkspaceId(),
                TeamCode.of(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                TeamStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public WorkspaceTeamJpaEntity toJpaEntity(WorkspaceTeam domain) {
        WorkspaceTeamJpaEntity entity = new WorkspaceTeamJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setCode(domain.code().value());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt()); // required for Spring Data merge()
        return entity;
    }
}
