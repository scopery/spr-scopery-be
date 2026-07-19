package com.company.scopery.modules.ratecard.resolution.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record ResolveRateQuery(
        UUID workspaceId,
        UUID organizationId,
        UUID projectId,
        UUID costRoleId,
        String costRoleCode,
        LocalDate targetDate,
        String currencyCode,
        String rateType
) {}
