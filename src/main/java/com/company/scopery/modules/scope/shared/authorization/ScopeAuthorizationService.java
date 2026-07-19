package com.company.scopery.modules.scope.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class ScopeAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public ScopeAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization){ this.projectAuthorization=projectAuthorization; }
    public void requireScopeView(UUID projectId){ require(projectId, IamAuthorities.SCOPE_VIEW); }
    public void requireScopeCreate(UUID projectId){ require(projectId, IamAuthorities.SCOPE_CREATE); }
    public void requireScopeUpdate(UUID projectId){ require(projectId, IamAuthorities.SCOPE_UPDATE); }
    public void requireScopeApprove(UUID projectId){ require(projectId, IamAuthorities.SCOPE_APPROVE); }
    public void requireDeliverableView(UUID projectId){ require(projectId, IamAuthorities.DELIVERABLE_VIEW); }
    public void requireDeliverableCreate(UUID projectId){ require(projectId, IamAuthorities.DELIVERABLE_CREATE); }
    public void requireDeliverableUpdate(UUID projectId){ require(projectId, IamAuthorities.DELIVERABLE_UPDATE); }
    public void requireDeliverableAccept(UUID projectId){ require(projectId, IamAuthorities.DELIVERABLE_ACCEPT); }
    public void requireDeliverableReopen(UUID projectId){ require(projectId, IamAuthorities.DELIVERABLE_REOPEN); }
    private void require(UUID projectId, IamPermissionAction a){
        try { projectAuthorization.requireProjectPermission(projectId, a); }
        catch (RuntimeException ex){ throw ScopeExceptions.accessDenied(); }
    }
}
