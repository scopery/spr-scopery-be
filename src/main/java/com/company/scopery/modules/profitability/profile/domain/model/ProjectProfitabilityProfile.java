package com.company.scopery.modules.profitability.profile.domain.model;
import java.time.Instant; import java.util.UUID;
public record ProjectProfitabilityProfile(UUID id, UUID workspaceId, UUID projectId, String currency, String trackingMode,
        String revenueMode, String costMode, UUID ownerUserId, String portalVisibility, String status,
        int version, Instant createdAt, Instant updatedAt) {
    public static ProjectProfitabilityProfile create(UUID workspaceId, UUID projectId, String currency, UUID ownerUserId) {
        Instant now = Instant.now();
        return new ProjectProfitabilityProfile(UUID.randomUUID(), workspaceId, projectId, currency, "AUTO", "QUOTE_AND_CR", "EFFORT_AND_RATE",
                ownerUserId, "NONE", "ACTIVE", 0, now, now);
    }
    public ProjectProfitabilityProfile disable() {
        return new ProjectProfitabilityProfile(id, workspaceId, projectId, currency, trackingMode, revenueMode, costMode, ownerUserId, portalVisibility, "DISABLED", version, createdAt, Instant.now());
    }
}
