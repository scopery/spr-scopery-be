package com.company.scopery.modules.workspace.orgteam.application.command;

import java.util.UUID;

public record AssignOrgTeamToWorkspaceCommand(
        UUID organizationId,
        UUID teamId,
        UUID workspaceId) {
}
