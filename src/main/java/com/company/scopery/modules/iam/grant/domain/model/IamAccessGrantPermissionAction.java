package com.company.scopery.modules.iam.grant.domain.model;

import java.time.Instant;
import java.util.UUID;

public record IamAccessGrantPermissionAction(
        UUID grantId,
        UUID permissionActionId,
        Instant createdAt) {

    public static IamAccessGrantPermissionAction create(UUID grantId, UUID permissionActionId) {
        return new IamAccessGrantPermissionAction(grantId, permissionActionId, Instant.now());
    }
}
