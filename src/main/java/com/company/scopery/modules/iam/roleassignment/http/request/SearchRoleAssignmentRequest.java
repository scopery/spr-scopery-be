package com.company.scopery.modules.iam.roleassignment.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Search/filter parameters for listing IAM role assignments")
public record SearchRoleAssignmentRequest(
        @Schema(description = "Filter by role ID to find all assignees of a specific role", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID roleId,

        @Schema(description = "Filter by assignee ID to find all roles assigned to a specific user or team", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID assigneeId,

        @Schema(description = "Filter by assignee type", example = "USER", allowableValues = {"USER", "TEAM"}, nullable = true)
        String assigneeType,

        @Schema(description = "Filter by assignment status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"}, nullable = true)
        String status,

        @Schema(description = "Filter by workspace ID to find assignments scoped to a specific workspace", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size (number of items per page)", example = "20")
        int size) {

    public SearchRoleAssignmentRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
