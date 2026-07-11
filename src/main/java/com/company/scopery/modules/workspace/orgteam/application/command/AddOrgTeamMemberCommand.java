package com.company.scopery.modules.workspace.orgteam.application.command;

import java.util.UUID;

public record AddOrgTeamMemberCommand(
        UUID teamId,
        UUID organizationId,
        UUID userId) {
}
