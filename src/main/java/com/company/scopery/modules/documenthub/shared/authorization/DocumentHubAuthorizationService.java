package com.company.scopery.modules.documenthub.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class DocumentHubAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public DocumentHubAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) { this.projectAuthorization = projectAuthorization; }
    public void requireView(UUID projectId) { requireProject(projectId, IamAuthorities.DOCUMENT_HUB_VIEW); }
    public void requireCreate(UUID projectId) { requireProject(projectId, IamAuthorities.DOCUMENT_HUB_CREATE); }
    public void requireUpdate(UUID projectId) { requireProject(projectId, IamAuthorities.DOCUMENT_HUB_UPDATE); }
    public void requireApprove(UUID projectId) { requireProject(projectId, IamAuthorities.DOCUMENT_HUB_APPROVE); }
    public void requireWorkspaceView(UUID workspaceId) { requireWorkspace(workspaceId, IamAuthorities.DOCUMENT_HUB_VIEW); }
    public void requireWorkspaceCreate(UUID workspaceId) { requireWorkspace(workspaceId, IamAuthorities.DOCUMENT_HUB_CREATE); }
    private void requireProject(UUID projectId, IamPermissionAction a) {
        try { projectAuthorization.requireProjectPermission(projectId, a); }
        catch (RuntimeException ex) { throw DocumentHubExceptions.accessDenied(); }
    }
    private void requireWorkspace(UUID workspaceId, IamPermissionAction a) {
        try { projectAuthorization.requireWorkspacePermission(workspaceId, a); }
        catch (RuntimeException ex) { throw DocumentHubExceptions.accessDenied(); }
    }
}
