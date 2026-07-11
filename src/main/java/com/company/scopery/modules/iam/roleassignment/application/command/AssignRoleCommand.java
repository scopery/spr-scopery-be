package com.company.scopery.modules.iam.roleassignment.application.command;

import java.util.UUID;

public record AssignRoleCommand(
        String assigneeType,
        UUID assigneeId,
        UUID roleId,
        UUID workspaceId) {
}
