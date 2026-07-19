package com.company.scopery.modules.iam.roleassignment.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to assign an IAM role to a user or team")
public record AssignRoleRequest(
        @Schema(description = "Type of assignee receiving the role", example = "USER", allowableValues = {"USER", "TEAM"})
        @NotBlank String assigneeType,

        @Schema(description = "ID of the assignee (user or team) who will receive the role", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID assigneeId,

        @Schema(description = "ID of the role to assign", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID roleId,

        @Schema(description = "Workspace to scope this assignment to (null for system-wide assignments)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId) {
}
