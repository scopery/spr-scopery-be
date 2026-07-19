package com.company.scopery.modules.clientportal.portalproject.application.service;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrantRepository;
import com.company.scopery.modules.clientportal.portalproject.application.response.PortalProjectSummaryResponse;
import com.company.scopery.modules.clientportal.review.application.response.ClientReviewRequestResponse;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewRequestRepository;
import com.company.scopery.modules.clientportal.shared.security.CurrentPortalAccountService;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class PortalProjectQueryService {
    private final ExternalProjectAccessGrantRepository grants;
    private final ClientReviewRequestRepository reviews;
    private final CurrentPortalAccountService currentPortalAccount;
    private final PortalGrantEnforcementService grantEnforcement;
    public PortalProjectQueryService(ExternalProjectAccessGrantRepository grants, ClientReviewRequestRepository reviews,
            CurrentPortalAccountService currentPortalAccount, PortalGrantEnforcementService grantEnforcement) {
        this.grants=grants; this.reviews=reviews; this.currentPortalAccount=currentPortalAccount; this.grantEnforcement=grantEnforcement;
    }
    @Transactional(readOnly=true)
    public List<PortalProjectSummaryResponse> listMyProjects() {
        var account = currentPortalAccount.requireCurrentPortalAccount();
        return grants.findByPortalAccountId(account.id()).stream()
                .filter(g -> g.isEffective())
                .map(g -> new PortalProjectSummaryResponse(g.projectId(), g.workspaceId(), g.permissionPolicyCode(), g.status().name()))
                .toList();
    }
    @Transactional(readOnly=true)
    public List<ClientReviewRequestResponse> listReviews(UUID projectId) {
        grantEnforcement.requireActiveGrant(projectId);
        return reviews.findByProjectId(projectId).stream().map(ClientReviewRequestResponse::from).toList();
    }
}
