package com.company.scopery.modules.iam.roleassignment.infrastructure.mapper;

import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.enums.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.enums.RoleAssigneeType;
import com.company.scopery.modules.iam.roleassignment.infrastructure.persistence.IamRoleAssignmentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamRoleAssignmentPersistenceMapper {

    public IamRoleAssignment toDomain(IamRoleAssignmentJpaEntity e) {
        return new IamRoleAssignment(
                e.getId(),
                RoleAssigneeType.valueOf(e.getAssigneeType()),
                e.getAssigneeId(),
                e.getRoleId(),
                e.getWorkspaceId(),
                e.getAssignedBy(),
                e.getAssignedAt(),
                IamRoleAssignmentStatus.valueOf(e.getStatus()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public IamRoleAssignmentJpaEntity toJpaEntity(IamRoleAssignment domain) {
        IamRoleAssignmentJpaEntity e = new IamRoleAssignmentJpaEntity();
        e.setId(domain.id());
        e.setAssigneeType(domain.assigneeType().name());
        e.setAssigneeId(domain.assigneeId());
        e.setRoleId(domain.roleId());
        e.setWorkspaceId(domain.workspaceId());
        e.setAssignedBy(domain.assignedBy());
        e.setAssignedAt(domain.assignedAt());
        e.setStatus(domain.status().name());
        e.setCreatedAt(domain.createdAt());
        return e;
    }
}
