package com.company.scopery.modules.workspace.orgteam.application.command;

import java.util.UUID;

public record CreateOrgTeamCommand(
        UUID organizationId,
        String name,
        String code,
        String description) {
}
