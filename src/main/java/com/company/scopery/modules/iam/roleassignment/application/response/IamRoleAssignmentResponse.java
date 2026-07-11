package com.company.scopery.modules.iam.roleassignment.application.response;

import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;

import java.time.Instant;
import java.util.UUID;

public record IamRoleAssignmentResponse(
        UUID id,
        String assigneeType,
        UUID assigneeId,
        UUID roleId,
        UUID workspaceId,
        UUID assignedBy,
        Instant assignedAt,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamRoleAssignmentResponse from(IamRoleAssignment domain) {
        return new IamRoleAssignmentResponse(
                domain.id(),
                domain.assigneeType().name(),
                domain.assigneeId(),
                domain.roleId(),
                domain.workspaceId(),
                domain.assignedBy(),
                domain.assignedAt(),
                domain.status().name(),
                domain.createdAt(),
                domain.updatedAt()
        );
    }
}
