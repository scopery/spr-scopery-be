package com.company.scopery.modules.iam.role.http.request;

import java.util.UUID;

public record SearchIamRoleRequest(
        String keyword,
        UUID workspaceId,
        String roleScope,
        String roleSource,
        String status,
        boolean includeDeleted,
        int page,
        int size) {
    public SearchIamRoleRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
