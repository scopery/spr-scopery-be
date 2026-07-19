package com.company.scopery.modules.productivity.shared.authorization;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.productivity.shared.error.ProductivityExceptions;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class ProductivityAuthorizationService {
    private final WorkspaceIamIntegrationService iam;
    private final CurrentUserAuthorizationService currentUser;
    public ProductivityAuthorizationService(WorkspaceIamIntegrationService iam, CurrentUserAuthorizationService currentUser) {
        this.iam = iam; this.currentUser = currentUser;
    }
    public void requireSearch(UUID workspaceId) { require(workspaceId, IamAuthorities.GLOBAL_SEARCH_USE); }
    public void requireSavedSearchManage(UUID workspaceId) { require(workspaceId, IamAuthorities.SAVED_SEARCH_CREATE); }
    public void requireFavoriteManage(UUID workspaceId) { require(workspaceId, IamAuthorities.FAVORITE_MANAGE); }
    public void requireSavedViewManage(UUID workspaceId) { require(workspaceId, IamAuthorities.SAVED_VIEW_CREATE); }
    public void requirePinManage(UUID workspaceId) { require(workspaceId, IamAuthorities.FAVORITE_MANAGE); }
    public void requireInboxView(UUID workspaceId) { require(workspaceId, IamAuthorities.WORK_INBOX_VIEW); }
    public void requireNavView(UUID workspaceId) { require(workspaceId, IamAuthorities.NAVIGATION_VIEW); }
    private void require(UUID workspaceId, IamPermissionAction a) {
        try { iam.requireWorkspaceAccess(workspaceId, currentUser.resolveCurrentUser().id(), a); }
        catch (RuntimeException ex) { throw ProductivityExceptions.accessDenied(); }
    }
}
