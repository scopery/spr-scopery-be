package com.company.scopery.modules.iam.roleassignment.application.response;

import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "IAM role assignment record representing a role granted to a specific user or team")
public record IamRoleAssignmentResponse(
        @Schema(description = "Unique identifier of this role assignment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Type of assignee this role was granted to", example = "USER", allowableValues = {"USER", "TEAM"})
        String assigneeType,

        @Schema(description = "ID of the assignee (user or team) who received this role", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID assigneeId,

        @Schema(description = "ID of the role that was assigned", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID roleId,

        @Schema(description = "Workspace this assignment is scoped to (null for system-wide assignments)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "ID of the user who performed this role assignment", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID assignedBy,

        @Schema(description = "Timestamp when the role was assigned", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant assignedAt,

        @Schema(description = "Current status of the assignment", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "Timestamp when this assignment record was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when this assignment record was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static IamRoleAssignmentResponse from(IamRoleAssignment domain) {
        return new IamRoleAssignmentResponse(
                domain.id(),
                domain.assigneeType().name(),
                domain.assigneeId(),
                domain.roleId(),
                domain.workspaceId(),
                domain.assignedBy(),
                domain.assignedAt(),
                domain.status().name(),
                domain.createdAt(),
                domain.updatedAt()
        );
    }
}
