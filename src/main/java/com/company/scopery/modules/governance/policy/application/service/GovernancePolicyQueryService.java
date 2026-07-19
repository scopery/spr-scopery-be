package com.company.scopery.modules.governance.policy.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.governance.policy.application.response.GovernancePolicyResponse;
import com.company.scopery.modules.governance.policy.domain.model.GovernancePolicyRepository;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class GovernancePolicyQueryService {
    private final GovernancePolicyRepository policies; private final WorkspaceIamIntegrationService iam; private final CurrentUserAuthorizationService currentUser;
    public GovernancePolicyQueryService(GovernancePolicyRepository policies, WorkspaceIamIntegrationService iam, CurrentUserAuthorizationService currentUser) {
        this.policies=policies; this.iam=iam; this.currentUser=currentUser;
    }
    @Transactional(readOnly=true)
    public List<GovernancePolicyResponse> list(UUID workspaceId) {
        try { iam.requireWorkspaceAccess(workspaceId, currentUser.resolveCurrentUser().id(), IamAuthorities.GOVERNANCE_POLICY_VIEW); }
        catch (RuntimeException ex) { throw GovernanceExceptions.accessDenied(); }
        return policies.findByWorkspaceId(workspaceId).stream().map(GovernancePolicyResponse::from).toList();
    }
}
