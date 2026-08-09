package com.company.scopery.modules.profitability.profile.domain.model;

import com.company.scopery.modules.profitability.profile.domain.enums.ProfitabilityProfileStatus;

import java.time.Instant;
import java.util.UUID;

public record ProfitabilityProfile(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        String currency,
        ProfitabilityProfileStatus status,
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitabilityProfile create(UUID projectId, UUID workspaceId, String currency) {
        return new ProfitabilityProfile(
                UUID.randomUUID(),
                projectId,
                workspaceId,
                currency,
                ProfitabilityProfileStatus.ACTIVE,
                null,
                null,
                null
        );
    }
}
