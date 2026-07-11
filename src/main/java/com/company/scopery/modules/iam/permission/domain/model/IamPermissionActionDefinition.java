package com.company.scopery.modules.iam.permission.domain.model;

import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;

import java.time.Instant;
import java.util.UUID;

public record IamPermissionActionDefinition(
        UUID id,
        UUID permissionId,
        String actionCode,
        String name,
        String description,
        UUID rightId,
        IamPermissionStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamPermissionActionDefinition create(UUID permissionId, String actionCode,
                                                       String name, String description, UUID rightId) {
        Instant now = Instant.now();
        return new IamPermissionActionDefinition(UUID.randomUUID(), permissionId, normalizeActionCode(actionCode),
                name, description, rightId, IamPermissionStatus.ACTIVE, now, now);
    }

    public IamPermissionActionDefinition updateBackingRight(UUID rightId) {
        return new IamPermissionActionDefinition(id, permissionId, actionCode, name, description,
                rightId, status, createdAt, Instant.now());
    }

    public IamPermissionActionDefinition syncCatalog(String name, String description, UUID rightId) {
        return new IamPermissionActionDefinition(id, permissionId, actionCode, name, description,
                rightId, IamPermissionStatus.ACTIVE, createdAt, Instant.now());
    }

    public IamPermissionActionDefinition deactivate() {
        return new IamPermissionActionDefinition(id, permissionId, actionCode, name, description,
                rightId, IamPermissionStatus.INACTIVE, createdAt, Instant.now());
    }

    private static String normalizeActionCode(String actionCode) {
        if (actionCode == null || actionCode.isBlank()) {
            throw new IllegalArgumentException("Action code must not be blank");
        }
        String trimmed = actionCode.trim().toUpperCase();
        if (!trimmed.matches("[A-Z0-9_]+")) {
            throw new IllegalArgumentException("Action code must contain only uppercase letters, digits, or underscores");
        }
        return trimmed;
    }
}
