package com.company.scopery.modules.clientportal.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.*;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class ClientPortalAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public ClientPortalAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization){this.projectAuthorization=projectAuthorization;}
    public void requireManage(UUID projectId){require(projectId, IamAuthorities.CLIENT_PORTAL_MANAGE);}
    public void requireView(UUID projectId){require(projectId, IamAuthorities.CLIENT_PORTAL_VIEW);}
    private void require(UUID projectId, IamPermissionAction a){
        try{projectAuthorization.requireProjectPermission(projectId,a);}catch(RuntimeException ex){throw ClientPortalExceptions.accessDenied();}
    }
}
