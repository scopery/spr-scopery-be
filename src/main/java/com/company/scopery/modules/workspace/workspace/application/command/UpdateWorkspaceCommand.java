package com.company.scopery.modules.workspace.workspace.application.command;

import java.util.UUID;

public record UpdateWorkspaceCommand(
        UUID id,
        String name,
        String description,
        String defaultVisibility,
        String joinPolicy) {
}
