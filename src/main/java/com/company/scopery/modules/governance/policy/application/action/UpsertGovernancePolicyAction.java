package com.company.scopery.modules.governance.policy.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.governance.policy.application.command.UpsertGovernancePolicyCommand;
import com.company.scopery.modules.governance.policy.application.response.GovernancePolicyResponse;
import com.company.scopery.modules.governance.policy.domain.model.*;
import com.company.scopery.modules.governance.shared.activity.GovernanceActivityLogger;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpsertGovernancePolicyAction {
    private final GovernancePolicyRepository policies; private final WorkspaceIamIntegrationService iam;
    private final CurrentUserAuthorizationService currentUser; private final GovernanceActivityLogger activityLogger;
    public UpsertGovernancePolicyAction(GovernancePolicyRepository policies, WorkspaceIamIntegrationService iam,
                                        CurrentUserAuthorizationService currentUser, GovernanceActivityLogger activityLogger) {
        this.policies=policies; this.iam=iam; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public GovernancePolicyResponse execute(UpsertGovernancePolicyCommand c) {
        try { iam.requireWorkspaceAccess(c.workspaceId(), currentUser.resolveCurrentUser().id(), IamAuthorities.GOVERNANCE_POLICY_UPDATE); }
        catch (RuntimeException ex) { throw GovernanceExceptions.accessDenied(); }
        var existing = policies.findByWorkspaceAndObjectType(c.workspaceId(), c.objectTypeCode());
        GovernancePolicy saved;
        if (existing.isPresent()) {
            saved = policies.save(existing.get().update(c.versioningMode(), c.versionOnUpdate(), c.lockOnFinalize(), c.allowRestore(), c.allowOwnerGrant(), c.baselineGuardMode(), c.auditLevel()));
        } else {
            saved = policies.save(GovernancePolicy.create(c.workspaceId(), c.objectTypeCode()).update(c.versioningMode(), c.versionOnUpdate(), c.lockOnFinalize(), c.allowRestore(), c.allowOwnerGrant(), c.baselineGuardMode(), c.auditLevel()));
        }
        activityLogger.logSuccess("GOVERNANCE_POLICY", saved.id(), "GOVERNANCE_POLICY_UPSERTED", "Policy upserted");
        return GovernancePolicyResponse.from(saved);
    }
}
