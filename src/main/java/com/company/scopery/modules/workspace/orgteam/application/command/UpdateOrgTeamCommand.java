package com.company.scopery.modules.workspace.orgteam.application.command;

import java.util.UUID;

public record UpdateOrgTeamCommand(
        UUID teamId,
        UUID organizationId,
        String name,
        String description) {
}
