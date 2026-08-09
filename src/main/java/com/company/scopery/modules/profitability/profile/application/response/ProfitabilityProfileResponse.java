package com.company.scopery.modules.profitability.profile.application.response;

import com.company.scopery.modules.profitability.profile.domain.model.ProfitabilityProfile;

import java.util.UUID;

public record ProfitabilityProfileResponse(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        String currency,
        String status,
        String createdAt,
        String updatedAt
) {
    public static ProfitabilityProfileResponse from(ProfitabilityProfile p) {
        return new ProfitabilityProfileResponse(
                p.id(),
                p.projectId(),
                p.workspaceId(),
                p.currency(),
                p.status().name(),
                p.createdAt() != null ? p.createdAt().toString() : null,
                p.updatedAt() != null ? p.updatedAt().toString() : null
        );
    }
}
