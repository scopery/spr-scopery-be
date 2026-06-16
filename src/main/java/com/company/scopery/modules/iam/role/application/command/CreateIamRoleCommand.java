package com.company.scopery.modules.iam.role.application.command;

import java.util.UUID;

public record CreateIamRoleCommand(
        String code,
        String name,
        String description,
        String roleScope,
        String roleSource,
        UUID workspaceId,
        UUID parentRoleId) {}
