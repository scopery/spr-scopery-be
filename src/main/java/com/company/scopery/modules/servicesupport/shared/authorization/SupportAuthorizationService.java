package com.company.scopery.modules.servicesupport.shared.authorization;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class SupportAuthorizationService {
    private final CurrentUserAuthorizationService currentUserService; private final WorkspaceIamIntegrationService iam;
    public SupportAuthorizationService(CurrentUserAuthorizationService currentUserService, WorkspaceIamIntegrationService iam){this.currentUserService=currentUserService;this.iam=iam;}
    public void requireManage(UUID workspaceId){
        try { iam.requireWorkspaceAccess(workspaceId, currentUserService.resolveCurrentUser().id(), IamAuthorities.WORKSPACE_MANAGE); }
        catch (RuntimeException ex) { throw SupportExceptions.accessDenied(); }
    }
    public void requireView(UUID workspaceId){
        try { iam.requireWorkspaceAccess(workspaceId, currentUserService.resolveCurrentUser().id(), IamAuthorities.WORKSPACE_VIEW); }
        catch (RuntimeException ex) { throw SupportExceptions.accessDenied(); }
    }
}
