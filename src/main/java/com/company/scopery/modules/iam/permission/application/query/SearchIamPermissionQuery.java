package com.company.scopery.modules.iam.permission.application.query;

public record SearchIamPermissionQuery(
        String keyword,
        String moduleCode,
        String resourceScopeLevel,
        String dataAccessPolicy,
        String permissionCategory,
        String riskLevel,
        String assignableSubjectType,
        String status,
        int page,
        int size) {
}
