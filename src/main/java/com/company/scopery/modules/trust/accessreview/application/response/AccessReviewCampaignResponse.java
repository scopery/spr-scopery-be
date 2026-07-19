package com.company.scopery.modules.trust.accessreview.application.response;
import com.company.scopery.modules.trust.accessreview.domain.model.AccessReviewCampaign;
import java.time.Instant; import java.util.UUID;
public record AccessReviewCampaignResponse(UUID id, UUID workspaceId, String name, String status,
        Instant startedAt, Instant completedAt, Instant createdAt) {
    public static AccessReviewCampaignResponse from(AccessReviewCampaign c) {
        return new AccessReviewCampaignResponse(c.id(), c.workspaceId(), c.name(), c.status(),
                c.startedAt(), c.completedAt(), c.createdAt());
    }
}
