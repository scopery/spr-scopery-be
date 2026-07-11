package com.company.scopery.modules.workspace.orgteam.application.command;

import java.util.UUID;

public record RevokeOrgTeamWorkspaceAssignmentCommand(
        UUID organizationId,
        UUID assignmentId) {
}
