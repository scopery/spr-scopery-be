package com.company.scopery.modules.raid.raiditem.application.command;

import java.util.UUID;

public record UpdateRaidItemCommand(
        UUID projectId,
        UUID raidItemId,
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
