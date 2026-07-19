package com.company.scopery.modules.trust.accessreview.application.action;
import com.company.scopery.modules.trust.accessreview.application.response.AccessReviewCampaignResponse;
import com.company.scopery.modules.trust.accessreview.domain.model.AccessReviewCampaignRepository;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.*;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class StartAccessReviewCampaignAction {
    private final AccessReviewCampaignRepository repo;
    private final TrustAuthorizationService auth;
    private final TrustActivityLogger activity;
    public StartAccessReviewCampaignAction(AccessReviewCampaignRepository repo, TrustAuthorizationService auth, TrustActivityLogger activity) {
        this.repo = repo; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public AccessReviewCampaignResponse execute(UUID workspaceId, UUID campaignId) {
        auth.requireManage(workspaceId);
        var c = repo.findById(campaignId).orElseThrow(TrustExceptions::accessReviewNotFound);
        if (!workspaceId.equals(c.workspaceId())) throw TrustExceptions.accessReviewNotFound();
        try {
            var saved = repo.save(c.start());
            activity.logSuccess(TrustEntityTypes.ACCESS_REVIEW_CAMPAIGN, saved.id(), TrustActivityActions.ACCESS_REVIEW_CAMPAIGN_STARTED, "Access review started");
            return AccessReviewCampaignResponse.from(saved);
        } catch (IllegalStateException ex) { throw TrustExceptions.accessReviewInvalidStatus(); }
    }
}
