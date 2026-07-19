package com.company.scopery.modules.raid.raiditem.http.request;

import java.util.UUID;

public record UpdateRaidItemRequest(
        String title,
        String description,
        UUID ownerUserId,
        String probability,
        String impact,
        String riskResponseStrategy,
        String riskTrigger,
        String severity,
        String issueCategory,
        String impactSummary,
        String rootCause,
        String resolutionPlan,
        String assumptionStatement,
        String validationStatus,
        String dependencyType
) {}
