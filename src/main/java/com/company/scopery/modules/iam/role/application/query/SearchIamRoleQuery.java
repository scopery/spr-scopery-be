package com.company.scopery.modules.iam.role.application.query;

import java.util.UUID;

public record SearchIamRoleQuery(
        String keyword,
        UUID workspaceId,
        String roleScope,
        String roleSource,
        String status,
        boolean includeDeleted,
        int page,
        int size) {}
