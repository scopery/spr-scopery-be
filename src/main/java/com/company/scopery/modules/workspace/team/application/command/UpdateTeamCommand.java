package com.company.scopery.modules.workspace.team.application.command;

import java.util.UUID;

public record UpdateTeamCommand(
        UUID workspaceId,
        UUID teamId,
        String name,
        String description) {
}
