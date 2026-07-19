package com.company.scopery.modules.profitability.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class ProfitabilityAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public ProfitabilityAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization){this.projectAuthorization=projectAuthorization;}
    public void requireView(UUID projectId){ require(projectId, IamAuthorities.PROFITABILITY_PROFILE_VIEW); }
    public void requireUpdate(UUID projectId){ require(projectId, IamAuthorities.PROFITABILITY_PROFILE_UPDATE); }
    public void requireSummary(UUID projectId){ require(projectId, IamAuthorities.PROFITABILITY_SUMMARY_VIEW); }
    private void require(UUID projectId, com.company.scopery.modules.iam.shared.constant.IamPermissionAction a){
        try { projectAuthorization.requireProjectPermission(projectId, a); }
        catch (RuntimeException ex) { throw ProfitabilityExceptions.accessDenied(); }
    }
}
