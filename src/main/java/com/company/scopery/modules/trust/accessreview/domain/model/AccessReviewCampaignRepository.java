package com.company.scopery.modules.trust.accessreview.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface AccessReviewCampaignRepository {
    AccessReviewCampaign save(AccessReviewCampaign c);
    Optional<AccessReviewCampaign> findById(UUID id);
    List<AccessReviewCampaign> findByWorkspaceId(UUID workspaceId);
}
