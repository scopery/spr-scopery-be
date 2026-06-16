package com.company.scopery.modules.iam.roleassignment.api.request;

import java.util.UUID;

public record SearchRoleAssignmentRequest(
        UUID roleId,
        UUID assigneeId,
        String assigneeType,
        String status,
        UUID workspaceId,
        int page,
        int size) {

    public SearchRoleAssignmentRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
