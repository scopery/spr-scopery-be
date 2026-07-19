package com.company.scopery.modules.trust.shared.authorization;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class TrustAuthorizationService {
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    public TrustAuthorizationService(CurrentUserAuthorizationService currentUserService, WorkspaceIamIntegrationService iamIntegrationService) {
        this.currentUserService=currentUserService; this.iamIntegrationService=iamIntegrationService;
    }
    public void requireManage(UUID workspaceId) {
        try {
            UUID userId = currentUserService.resolveCurrentUser().id();
            iamIntegrationService.requireWorkspaceAccess(workspaceId, userId, IamAuthorities.WORKSPACE_MANAGE);
        } catch (RuntimeException ex) { throw TrustExceptions.accessDenied(); }
    }
    public void requireView(UUID workspaceId) {
        try {
            UUID userId = currentUserService.resolveCurrentUser().id();
            iamIntegrationService.requireWorkspaceAccess(workspaceId, userId, IamAuthorities.WORKSPACE_VIEW);
        } catch (RuntimeException ex) { throw TrustExceptions.accessDenied(); }
    }
}
