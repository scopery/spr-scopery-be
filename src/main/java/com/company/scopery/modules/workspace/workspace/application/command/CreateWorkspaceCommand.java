package com.company.scopery.modules.workspace.workspace.application.command;

import java.util.UUID;

public record CreateWorkspaceCommand(
        UUID organizationId,
        String name,
        String code,
        String description,
        String defaultVisibility,
        String joinPolicy) {
}
