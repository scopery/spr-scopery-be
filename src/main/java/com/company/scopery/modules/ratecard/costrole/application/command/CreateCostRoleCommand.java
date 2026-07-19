package com.company.scopery.modules.ratecard.costrole.application.command;

import java.util.UUID;

public record CreateCostRoleCommand(
        String code, String name, String description, String scope,
        UUID organizationId, UUID workspaceId, String category
) {}
