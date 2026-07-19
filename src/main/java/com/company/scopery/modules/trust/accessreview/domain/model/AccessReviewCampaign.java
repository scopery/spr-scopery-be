package com.company.scopery.modules.trust.accessreview.domain.model;
import java.time.Instant; import java.util.UUID;
public record AccessReviewCampaign(UUID id, UUID workspaceId, String name, String status, Instant startedAt, Instant completedAt, int version, Instant createdAt) {
    public static AccessReviewCampaign draft(UUID workspaceId, String name) {
        return new AccessReviewCampaign(UUID.randomUUID(), workspaceId, name, "DRAFT", null, null, 0, Instant.now());
    }
    public AccessReviewCampaign start() {
        if (!"DRAFT".equals(status)) throw new IllegalStateException("invalid");
        return new AccessReviewCampaign(id, workspaceId, name, "ACTIVE", Instant.now(), null, version, createdAt);
    }
    public AccessReviewCampaign complete() {
        if (!"ACTIVE".equals(status)) throw new IllegalStateException("invalid");
        return new AccessReviewCampaign(id, workspaceId, name, "COMPLETED", startedAt, Instant.now(), version, createdAt);
    }
    public AccessReviewCampaign cancel() {
        if ("COMPLETED".equals(status) || "CANCELLED".equals(status))
            throw new IllegalStateException("Cannot cancel in current status");
        return new AccessReviewCampaign(id, workspaceId, name, "CANCELLED", startedAt, null, version, createdAt);
    }
}
