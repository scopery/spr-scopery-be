package com.company.scopery.modules.profitability.plan.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ProfitabilityPlan(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID profitabilityProfileId,
        String planCode,
        String name,
        String planType,
        String status,
        UUID currentVersionId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitabilityPlan create(
            UUID workspaceId,
            UUID projectId,
            UUID profileId,
            String planCode,
            String name,
            String planType) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Plan name is required");
        }
        if (planType == null || planType.isBlank()) {
            throw new IllegalArgumentException("Plan type is required");
        }
        Instant now = Instant.now();
        return new ProfitabilityPlan(
                UUID.randomUUID(), workspaceId, projectId, profileId,
                planCode, name.trim(), planType, "DRAFT", null, 0, now, now);
    }

    public ProfitabilityPlan activate(UUID versionId) {
        return new ProfitabilityPlan(
                id, workspaceId, projectId, profitabilityProfileId,
                planCode, name, planType, "ACTIVE", versionId, version, createdAt, Instant.now());
    }

    public ProfitabilityPlan archive() {
        return new ProfitabilityPlan(
                id, workspaceId, projectId, profitabilityProfileId,
                planCode, name, planType, "ARCHIVED", currentVersionId, version, createdAt, Instant.now());
    }
}
