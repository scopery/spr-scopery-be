package com.company.scopery.modules.configuration.shared.authorization;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class ConfigurationAuthorizationService {
    private final WorkspaceIamIntegrationService iam; private final CurrentUserAuthorizationService currentUser;
    public ConfigurationAuthorizationService(WorkspaceIamIntegrationService iam, CurrentUserAuthorizationService currentUser){this.iam=iam;this.currentUser=currentUser;}
    public void requireFieldView(UUID workspaceId){require(workspaceId, IamAuthorities.CUSTOM_FIELD_VIEW);}
    public void requireFieldCreate(UUID workspaceId){require(workspaceId, IamAuthorities.CUSTOM_FIELD_CREATE);}
    public void requireFieldUpdate(UUID workspaceId){require(workspaceId, IamAuthorities.CUSTOM_FIELD_UPDATE);}
    public void requireFormView(UUID workspaceId){require(workspaceId, IamAuthorities.CUSTOM_FORM_VIEW);}
    public void requireFormManage(UUID workspaceId){require(workspaceId, IamAuthorities.CUSTOM_FIELD_UPDATE);}
    private void require(UUID workspaceId, IamPermissionAction a){
        try { iam.requireWorkspaceAccess(workspaceId, currentUser.resolveCurrentUser().id(), a); }
        catch (RuntimeException ex) { throw ConfigurationExceptions.accessDenied(); }
    }
}
