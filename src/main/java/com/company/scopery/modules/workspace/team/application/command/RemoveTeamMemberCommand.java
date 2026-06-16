package com.company.scopery.modules.workspace.team.application.command;

import java.util.UUID;

public record RemoveTeamMemberCommand(
        UUID workspaceId,
        UUID teamId,
        UUID userId) {
}
