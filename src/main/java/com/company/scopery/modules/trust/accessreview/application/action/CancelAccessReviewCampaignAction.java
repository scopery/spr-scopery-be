package com.company.scopery.modules.trust.accessreview.application.action;
import com.company.scopery.modules.trust.accessreview.application.response.AccessReviewCampaignResponse;
import com.company.scopery.modules.trust.accessreview.domain.model.AccessReviewCampaignRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CancelAccessReviewCampaignAction {
    private final AccessReviewCampaignRepository repo;
    private final TrustAuthorizationService auth;
    public CancelAccessReviewCampaignAction(AccessReviewCampaignRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public AccessReviewCampaignResponse execute(UUID workspaceId, UUID campaignId) {
        auth.requireManage(workspaceId);
        var c = repo.findById(campaignId).orElseThrow(TrustExceptions::accessReviewNotFound);
        if (!workspaceId.equals(c.workspaceId())) throw TrustExceptions.accessReviewNotFound();
        try {
            return AccessReviewCampaignResponse.from(repo.save(c.cancel()));
        } catch (IllegalStateException ex) { throw TrustExceptions.accessReviewInvalidStatus(); }
    }
}
