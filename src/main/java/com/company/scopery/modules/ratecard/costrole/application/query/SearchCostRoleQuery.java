package com.company.scopery.modules.ratecard.costrole.application.query;

import java.util.UUID;

public record SearchCostRoleQuery(
        String scope, UUID organizationId, UUID workspaceId, String status,
        String category, String code, int page, int size
) {}
