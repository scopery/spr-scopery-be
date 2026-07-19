package com.company.scopery.modules.raid.raiditem.http.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateRaidItemRequest(
        @NotBlank String type,
        @NotBlank String title,
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
