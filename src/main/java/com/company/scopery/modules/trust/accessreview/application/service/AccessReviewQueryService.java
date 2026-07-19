package com.company.scopery.modules.trust.accessreview.application.service;
import com.company.scopery.modules.trust.accessreview.application.response.AccessReviewCampaignResponse;
import com.company.scopery.modules.trust.accessreview.application.response.PermissionReviewFindingResponse;
import com.company.scopery.modules.trust.accessreview.domain.model.AccessReviewCampaignRepository;
import com.company.scopery.modules.trust.accessreview.domain.model.PermissionReviewFindingRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AccessReviewQueryService {
    private final AccessReviewCampaignRepository campaigns;
    private final PermissionReviewFindingRepository findings;
    private final TrustAuthorizationService auth;
    public AccessReviewQueryService(AccessReviewCampaignRepository campaigns, PermissionReviewFindingRepository findings, TrustAuthorizationService auth) {
        this.campaigns = campaigns; this.findings = findings; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<AccessReviewCampaignResponse> listCampaigns(UUID workspaceId) {
        auth.requireView(workspaceId);
        return campaigns.findByWorkspaceId(workspaceId).stream().map(AccessReviewCampaignResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public AccessReviewCampaignResponse getCampaign(UUID workspaceId, UUID campaignId) {
        auth.requireView(workspaceId);
        return campaigns.findById(campaignId).map(AccessReviewCampaignResponse::from)
                .orElseThrow(TrustExceptions::accessReviewNotFound);
    }
    @Transactional(readOnly = true)
    public List<PermissionReviewFindingResponse> listFindings(UUID workspaceId) {
        auth.requireView(workspaceId);
        return findings.findByWorkspaceId(workspaceId).stream().map(PermissionReviewFindingResponse::from).toList();
    }
}
