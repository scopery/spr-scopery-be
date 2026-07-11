package com.company.scopery.modules.iam.permission.http.request;

public record SearchIamPermissionRequest(
        String keyword, String moduleCode, String resourceScopeLevel, String dataAccessPolicy,
        String permissionCategory, String riskLevel, String assignableSubjectType, String status,
        int page, int size) {
    public SearchIamPermissionRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
