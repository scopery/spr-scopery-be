package com.company.scopery.modules.iam.grant.application.command;

import java.util.UUID;

public record BootstrapOrganizationAccessCommand(UUID orgId, String orgName, UUID ownerUserId) {
}
