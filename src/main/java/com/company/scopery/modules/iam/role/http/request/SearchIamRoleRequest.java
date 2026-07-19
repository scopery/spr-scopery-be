package com.company.scopery.modules.iam.role.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Search/filter parameters for listing IAM roles")
public record SearchIamRoleRequest(
        @Schema(description = "Full-text keyword to search in code or name", example = "AI Platform", nullable = true)
        String keyword,

        @Schema(description = "Filter by workspace ID (only returns workspace-scoped roles for this workspace)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "Filter by role scope", example = "SYSTEM", allowableValues = {"SYSTEM", "WORKSPACE"}, nullable = true)
        String roleScope,

        @Schema(description = "Filter by role source (e.g. BUILT_IN, CUSTOM)", example = "CUSTOM", nullable = true)
        String roleSource,

        @Schema(description = "Filter by role status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"}, nullable = true)
        String status,

        @Schema(description = "Whether to include soft-deleted roles in the results", example = "false", nullable = true)
        Boolean includeDeleted,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size (number of items per page)", example = "20")
        int size) {

    public SearchIamRoleRequest {
        if (includeDeleted == null) includeDeleted = false;
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
