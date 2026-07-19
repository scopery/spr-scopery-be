package com.company.scopery.modules.trust.accessreview.application.action;
import com.company.scopery.modules.trust.accessreview.application.response.PermissionReviewFindingResponse;
import com.company.scopery.modules.trust.accessreview.domain.model.*;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreatePermissionReviewFindingAction {
    private final PermissionReviewFindingRepository findings;
    private final AccessReviewCampaignRepository campaigns;
    private final TrustAuthorizationService auth;
    public CreatePermissionReviewFindingAction(PermissionReviewFindingRepository findings, AccessReviewCampaignRepository campaigns,
                                               TrustAuthorizationService auth) {
        this.findings = findings; this.campaigns = campaigns; this.auth = auth;
    }
    @Transactional
    public PermissionReviewFindingResponse execute(UUID workspaceId, UUID campaignId, String findingType, String severity, String recommendation) {
        auth.requireManage(workspaceId);
        var c = campaigns.findById(campaignId).orElseThrow(TrustExceptions::accessReviewNotFound);
        if (!workspaceId.equals(c.workspaceId())) throw TrustExceptions.accessReviewNotFound();
        var saved = findings.save(PermissionReviewFinding.open(workspaceId, campaignId,
                findingType == null ? "EXCESSIVE_PERMISSION" : findingType,
                severity == null ? "MEDIUM" : severity,
                recommendation));
        return PermissionReviewFindingResponse.from(saved);
    }
}
