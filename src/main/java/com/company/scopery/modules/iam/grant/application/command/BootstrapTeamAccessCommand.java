package com.company.scopery.modules.iam.grant.application.command;

import java.util.UUID;

public record BootstrapTeamAccessCommand(
        UUID teamId, UUID workspaceId, String teamName, UUID ownerUserId) {
}
