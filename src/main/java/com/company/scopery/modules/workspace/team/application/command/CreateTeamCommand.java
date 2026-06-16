package com.company.scopery.modules.workspace.team.application.command;

import java.util.UUID;

public record CreateTeamCommand(
        UUID workspaceId,
        String name,
        String code,
        String description) {
}
