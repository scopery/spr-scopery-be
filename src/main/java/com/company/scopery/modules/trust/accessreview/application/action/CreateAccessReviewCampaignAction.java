package com.company.scopery.modules.trust.accessreview.application.action;
import com.company.scopery.modules.trust.accessreview.application.response.AccessReviewCampaignResponse;
import com.company.scopery.modules.trust.accessreview.domain.model.*;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.*;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateAccessReviewCampaignAction {
    private final AccessReviewCampaignRepository repo;
    private final TrustAuthorizationService auth;
    private final TrustActivityLogger activity;
    public CreateAccessReviewCampaignAction(AccessReviewCampaignRepository repo, TrustAuthorizationService auth, TrustActivityLogger activity) {
        this.repo = repo; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public AccessReviewCampaignResponse execute(UUID workspaceId, String name, String scopeJson) {
        auth.requireManage(workspaceId);
        var saved = repo.save(AccessReviewCampaign.draft(workspaceId, name == null || name.isBlank() ? "Access Review" : name));
        activity.logSuccess(TrustEntityTypes.ACCESS_REVIEW_CAMPAIGN, saved.id(), TrustActivityActions.ACCESS_REVIEW_CAMPAIGN_CREATED, "Access review campaign created");
        return AccessReviewCampaignResponse.from(saved);
    }
}
