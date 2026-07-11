package com.company.scopery.modules.workspace.orgteam.application.command;

import java.util.UUID;

public record ArchiveOrgTeamCommand(UUID teamId, UUID organizationId) {
}
