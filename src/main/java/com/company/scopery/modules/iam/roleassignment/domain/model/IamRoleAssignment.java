package com.company.scopery.modules.iam.roleassignment.domain.model;

import com.company.scopery.modules.iam.roleassignment.domain.enums.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.enums.RoleAssigneeType;

import java.time.Instant;
import java.util.UUID;

public record IamRoleAssignment(
        UUID id,
        RoleAssigneeType assigneeType,
        UUID assigneeId,
        UUID roleId,
        UUID workspaceId,
        UUID assignedBy,
        Instant assignedAt,
        IamRoleAssignmentStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamRoleAssignment create(RoleAssigneeType assigneeType, UUID assigneeId,
                                            UUID roleId, UUID workspaceId, UUID assignedBy) {
        Instant now = Instant.now();
        return new IamRoleAssignment(UUID.randomUUID(), assigneeType, assigneeId,
                roleId, workspaceId, assignedBy, now,
                IamRoleAssignmentStatus.ACTIVE, now, now);
    }

    public IamRoleAssignment activate() {
        return new IamRoleAssignment(id, assigneeType, assigneeId, roleId, workspaceId,
                assignedBy, assignedAt, IamRoleAssignmentStatus.ACTIVE, createdAt, Instant.now());
    }

    public IamRoleAssignment deactivate() {
        return new IamRoleAssignment(id, assigneeType, assigneeId, roleId, workspaceId,
                assignedBy, assignedAt, IamRoleAssignmentStatus.INACTIVE, createdAt, Instant.now());
    }
}
