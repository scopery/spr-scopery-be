package com.company.scopery.modules.iam.roleassignment.application.query;

import java.util.UUID;

public record SearchRoleAssignmentQuery(
        UUID roleId,
        UUID assigneeId,
        String assigneeType,
        String status,
        UUID workspaceId,
        int page,
        int size) {
}
