package com.company.scopery.modules.traceability.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.*;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class TraceabilityAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public TraceabilityAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization){this.projectAuthorization=projectAuthorization;}
    public void requireView(UUID projectId){requireProject(projectId, IamAuthorities.REQUIREMENT_VIEW);}
    public void requireCreate(UUID projectId){requireProject(projectId, IamAuthorities.REQUIREMENT_CREATE);}
    public void requireUpdate(UUID projectId){requireProject(projectId, IamAuthorities.REQUIREMENT_UPDATE);}
    public void requireApprove(UUID projectId){requireProject(projectId, IamAuthorities.REQUIREMENT_APPROVE);}
    public void requireWorkspaceView(UUID workspaceId){requireWorkspace(workspaceId, IamAuthorities.REQUIREMENT_VIEW);}
    public void requireWorkspaceCreate(UUID workspaceId){requireWorkspace(workspaceId, IamAuthorities.REQUIREMENT_CREATE);}
    private void requireProject(UUID projectId, IamPermissionAction a){
        try{projectAuthorization.requireProjectPermission(projectId,a);}catch(RuntimeException ex){throw TraceabilityExceptions.accessDenied();}
    }
    private void requireWorkspace(UUID workspaceId, IamPermissionAction a){
        try{projectAuthorization.requireWorkspacePermission(workspaceId,a);}catch(RuntimeException ex){throw TraceabilityExceptions.accessDenied();}
    }
}
