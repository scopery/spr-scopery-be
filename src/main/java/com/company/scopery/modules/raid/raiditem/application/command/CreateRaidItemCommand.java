package com.company.scopery.modules.raid.raiditem.application.command;

import java.util.UUID;

public record CreateRaidItemCommand(
        UUID projectId,
        String type,
        String title,
        String code,
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
