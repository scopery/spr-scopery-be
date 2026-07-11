package com.company.scopery.modules.iam.grant.application.command;

import java.util.UUID;

public record BootstrapOrganizationTeamAccessCommand(
        UUID teamId, UUID organizationId, String teamName, UUID ownerUserId) {
}
