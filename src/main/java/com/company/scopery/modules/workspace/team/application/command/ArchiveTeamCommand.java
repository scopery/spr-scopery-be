package com.company.scopery.modules.workspace.team.application.command;

import java.util.UUID;

public record ArchiveTeamCommand(UUID workspaceId, UUID teamId) {
}
