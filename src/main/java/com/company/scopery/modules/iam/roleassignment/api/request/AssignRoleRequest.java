package com.company.scopery.modules.iam.roleassignment.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignRoleRequest(
        @NotBlank String assigneeType,
        @NotNull UUID assigneeId,
        @NotNull UUID roleId,
        UUID workspaceId,
        UUID assignedBy) {
}
