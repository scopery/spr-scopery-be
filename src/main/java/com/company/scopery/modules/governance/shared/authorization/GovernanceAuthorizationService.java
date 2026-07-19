package com.company.scopery.modules.governance.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class GovernanceAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public GovernanceAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization){ this.projectAuthorization=projectAuthorization; }
    public void requirePolicyView(UUID projectId){ require(projectId, IamAuthorities.GOVERNANCE_POLICY_VIEW); }
    public void requirePolicyUpdate(UUID projectId){ require(projectId, IamAuthorities.GOVERNANCE_POLICY_UPDATE); }
    public void requireOwnershipAssign(UUID projectId){ require(projectId, IamAuthorities.OBJECT_OWNERSHIP_ASSIGN); }
    public void requireOwnershipView(UUID projectId){ require(projectId, IamAuthorities.OBJECT_OWNERSHIP_VIEW); }
    public void requireVersionView(UUID projectId){ require(projectId, IamAuthorities.OBJECT_VERSION_VIEW); }
    public void requireLockCreate(UUID projectId){ require(projectId, IamAuthorities.OBJECT_LOCK_CREATE); }
    public void requireReportView(UUID projectId){ require(projectId, IamAuthorities.GOVERNANCE_POLICY_VIEW); }
    private void require(UUID projectId, com.company.scopery.modules.iam.shared.constant.IamPermissionAction a){
        try { projectAuthorization.requireProjectPermission(projectId, a); }
        catch (RuntimeException ex) { throw GovernanceExceptions.accessDenied(); }
    }
}
