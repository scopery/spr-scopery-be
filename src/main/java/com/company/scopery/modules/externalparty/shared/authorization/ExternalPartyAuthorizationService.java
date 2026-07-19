package com.company.scopery.modules.externalparty.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.*;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class ExternalPartyAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public ExternalPartyAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization){this.projectAuthorization=projectAuthorization;}
    public void requireView(UUID projectId){requireProject(projectId, IamAuthorities.EXTERNAL_PARTY_VIEW);}
    public void requireCreate(UUID projectId){requireProject(projectId, IamAuthorities.EXTERNAL_PARTY_CREATE);}
    public void requireUpdate(UUID projectId){requireProject(projectId, IamAuthorities.EXTERNAL_PARTY_UPDATE);}
    public void requireWorkspaceView(UUID workspaceId){requireWorkspace(workspaceId, IamAuthorities.EXTERNAL_PARTY_VIEW);}
    public void requireWorkspaceCreate(UUID workspaceId){requireWorkspace(workspaceId, IamAuthorities.EXTERNAL_PARTY_CREATE);}
    public void requireWorkspaceUpdate(UUID workspaceId){requireWorkspace(workspaceId, IamAuthorities.EXTERNAL_PARTY_UPDATE);}
    private void requireProject(UUID projectId, IamPermissionAction a){
        try{projectAuthorization.requireProjectPermission(projectId,a);}catch(RuntimeException ex){throw ExternalPartyExceptions.accessDenied();}
    }
    private void requireWorkspace(UUID workspaceId, IamPermissionAction a){
        try{projectAuthorization.requireWorkspacePermission(workspaceId,a);}catch(RuntimeException ex){throw ExternalPartyExceptions.accessDenied();}
    }
}
