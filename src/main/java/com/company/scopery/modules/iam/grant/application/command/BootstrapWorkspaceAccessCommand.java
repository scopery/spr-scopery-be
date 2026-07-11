package com.company.scopery.modules.iam.grant.application.command;

import java.util.UUID;

public record BootstrapWorkspaceAccessCommand(
        UUID workspaceId, UUID organizationId, String workspaceName, UUID ownerUserId) {
}
