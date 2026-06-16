package com.company.scopery.modules.workspace.organization.application.command;

import java.util.UUID;

public record UpdateOrganizationCommand(
        UUID id,
        String name,
        String description) {
}
