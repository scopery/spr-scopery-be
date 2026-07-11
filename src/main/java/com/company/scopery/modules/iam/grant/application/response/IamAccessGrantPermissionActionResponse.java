package com.company.scopery.modules.iam.grant.application.response;

import java.time.Instant;
import java.util.UUID;

public record IamAccessGrantPermissionActionResponse(
        UUID grantId,
        UUID resourceId,
        UUID workspaceId,
        UUID permissionActionId,
        UUID permissionId,
        String permissionCode,
        String actionCode,
        UUID rightId,
        String legacyRightCode,
        Instant createdAt) {
}
