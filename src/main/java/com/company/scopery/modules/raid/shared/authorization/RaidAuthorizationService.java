package com.company.scopery.modules.raid.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class RaidAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public RaidAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization){ this.projectAuthorization=projectAuthorization; }
    public void requireView(UUID projectId){ require(projectId, IamAuthorities.RAID_VIEW); }
    public void requireCreate(UUID projectId){ require(projectId, IamAuthorities.RAID_CREATE); }
    public void requireUpdate(UUID projectId){ require(projectId, IamAuthorities.RAID_UPDATE); }
    public void requireEscalate(UUID projectId){ require(projectId, IamAuthorities.RAID_ESCALATE); }
    public void requireConvert(UUID projectId){ require(projectId, IamAuthorities.RAID_CONVERT); }
    public void requireArchive(UUID projectId){ require(projectId, IamAuthorities.RAID_ARCHIVE); }
    public void requireDecisionView(UUID projectId){ require(projectId, IamAuthorities.DECISION_VIEW); }
    public void requireDecisionCreate(UUID projectId){ require(projectId, IamAuthorities.DECISION_CREATE); }
    public void requireDecisionUpdate(UUID projectId){ require(projectId, IamAuthorities.DECISION_UPDATE); }
    public void requireDecide(UUID projectId){ require(projectId, IamAuthorities.DECISION_DECIDE); }
    public boolean canViewFinance(UUID projectId){
        try { projectAuthorization.requireProjectPermission(projectId, IamAuthorities.PROJECT_FINANCE_VIEW); return true; }
        catch (RuntimeException ex){ return false; }
    }
    private void require(UUID projectId, IamPermissionAction a){
        try { projectAuthorization.requireProjectPermission(projectId, a); }
        catch (RuntimeException ex){ throw RaidExceptions.accessDenied(); }
    }
}
