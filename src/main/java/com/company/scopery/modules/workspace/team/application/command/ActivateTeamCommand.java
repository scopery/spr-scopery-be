package com.company.scopery.modules.workspace.team.application.command;

import java.util.UUID;

public record ActivateTeamCommand(UUID workspaceId, UUID teamId) {
}
