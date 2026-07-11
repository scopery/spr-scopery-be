package com.company.scopery.modules.iam.role.application.response;

import com.company.scopery.modules.iam.role.domain.model.IamRole;

import java.time.Instant;
import java.util.UUID;

public record IamRoleResponse(
        UUID id,
        String code,
        String name,
        String description,
        String status,
        String roleScope,
        String roleSource,
        UUID workspaceId,
        UUID parentRoleId,
        boolean isSystem,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static IamRoleResponse from(IamRole role) {
        return new IamRoleResponse(
                role.id(),
                role.code().value(),
                role.name(),
                role.description(),
                role.status().name(),
                role.roleScope() != null ? role.roleScope().name() : null,
                role.roleSource() != null ? role.roleSource().name() : null,
                role.workspaceId(),
                role.parentRoleId(),
                role.isSystem(),
                role.deletedAt(),
                role.createdAt(),
                role.updatedAt());
    }
}
