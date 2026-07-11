package com.company.scopery.modules.iam.permission.application.response;

import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;

import java.time.Instant;
import java.util.UUID;

public record IamPermissionActionResponse(
        UUID id,
        UUID permissionId,
        String permissionCode,
        String actionCode,
        String name,
        String description,
        UUID rightId,
        String legacyRightCode,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamPermissionActionResponse from(IamPermissionActionDefinition action,
                                                   String permissionCode,
                                                   String legacyRightCode) {
        return new IamPermissionActionResponse(
                action.id(),
                action.permissionId(),
                permissionCode,
                action.actionCode(),
                action.name(),
                action.description(),
                action.rightId(),
                legacyRightCode,
                action.status().name(),
                action.createdAt(),
                action.updatedAt());
    }
}
