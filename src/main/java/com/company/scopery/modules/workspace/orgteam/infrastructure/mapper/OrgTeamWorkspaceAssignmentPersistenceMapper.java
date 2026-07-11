package com.company.scopery.modules.workspace.orgteam.infrastructure.mapper;

import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamWorkspaceAssignmentStatus;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignment;
import com.company.scopery.modules.workspace.orgteam.infrastructure.persistence.OrgTeamWorkspaceAssignmentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OrgTeamWorkspaceAssignmentPersistenceMapper {

    public OrgTeamWorkspaceAssignment toDomain(OrgTeamWorkspaceAssignmentJpaEntity entity) {
        return new OrgTeamWorkspaceAssignment(
                entity.getId(),
                entity.getTeamId(),
                entity.getWorkspaceId(),
                entity.getAssignedBy(),
                entity.getAssignedAt(),
                OrgTeamWorkspaceAssignmentStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public OrgTeamWorkspaceAssignmentJpaEntity toJpaEntity(OrgTeamWorkspaceAssignment domain) {
        OrgTeamWorkspaceAssignmentJpaEntity entity = new OrgTeamWorkspaceAssignmentJpaEntity();
        entity.setId(domain.id());
        entity.setTeamId(domain.teamId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setAssignedBy(domain.assignedBy());
        entity.setAssignedAt(domain.assignedAt());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
