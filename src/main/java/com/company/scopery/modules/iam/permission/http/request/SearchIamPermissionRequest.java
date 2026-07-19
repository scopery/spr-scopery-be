package com.company.scopery.modules.iam.permission.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Search/filter parameters for listing IAM permissions")
public record SearchIamPermissionRequest(
        @Schema(description = "Full-text keyword to search in code, name, or description", example = "AI Platform", nullable = true)
        String keyword,

        @Schema(description = "Filter by the module code that owns the permission (e.g. AIAGENT, IAM)", example = "AIAGENT", nullable = true)
        String moduleCode,

        @Schema(description = "Filter by resource scope level (e.g. GLOBAL, WORKSPACE, RESOURCE)", example = "RESOURCE", nullable = true)
        String resourceScopeLevel,

        @Schema(description = "Filter by data access policy (e.g. ALL, OWN, NONE)", example = "ALL", nullable = true)
        String dataAccessPolicy,

        @Schema(description = "Filter by permission category (e.g. ADMINISTRATIVE, OPERATIONAL, READ_ONLY)", example = "ADMINISTRATIVE", nullable = true)
        String permissionCategory,

        @Schema(description = "Filter by risk level (e.g. LOW, MEDIUM, HIGH, CRITICAL)", example = "HIGH", nullable = true)
        String riskLevel,

        @Schema(description = "Filter by assignable subject type (e.g. USER, TEAM)", example = "USER", nullable = true)
        String assignableSubjectType,

        @Schema(description = "Filter by permission status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"}, nullable = true)
        String status,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size (number of items per page)", example = "20")
        int size) {

    public SearchIamPermissionRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
