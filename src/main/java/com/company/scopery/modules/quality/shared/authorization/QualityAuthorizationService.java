package com.company.scopery.modules.quality.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class QualityAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public QualityAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }
    public void requireQualityView(UUID projectId) { require(projectId, IamAuthorities.QUALITY_VIEW); }
    public void requireQualityCreate(UUID projectId) { require(projectId, IamAuthorities.QUALITY_CREATE); }
    public void requireQualityUpdate(UUID projectId) { require(projectId, IamAuthorities.QUALITY_UPDATE); }
    public void requireQualityApprove(UUID projectId) { require(projectId, IamAuthorities.QUALITY_APPROVE); }
    public void requireTestView(UUID projectId) { require(projectId, IamAuthorities.TEST_VIEW); }
    public void requireTestCreate(UUID projectId) { require(projectId, IamAuthorities.TEST_CREATE); }
    public void requireTestUpdate(UUID projectId) { require(projectId, IamAuthorities.TEST_UPDATE); }
    public void requireTestExecute(UUID projectId) { require(projectId, IamAuthorities.TEST_EXECUTE); }
    public void requireDefectView(UUID projectId) { require(projectId, IamAuthorities.DEFECT_VIEW); }
    public void requireDefectCreate(UUID projectId) { require(projectId, IamAuthorities.DEFECT_CREATE); }
    public void requireDefectUpdate(UUID projectId) { require(projectId, IamAuthorities.DEFECT_UPDATE); }
    public void requireDefectResolve(UUID projectId) { require(projectId, IamAuthorities.DEFECT_RESOLVE); }
    public void requireReleaseView(UUID projectId) { require(projectId, IamAuthorities.RELEASE_VIEW); }
    public void requireReleaseCreate(UUID projectId) { require(projectId, IamAuthorities.RELEASE_CREATE); }
    public void requireReleaseUpdate(UUID projectId) { require(projectId, IamAuthorities.RELEASE_UPDATE); }
    public void requireReleaseApprove(UUID projectId) { require(projectId, IamAuthorities.RELEASE_APPROVE); }
    public void requireDeploymentView(UUID projectId) { require(projectId, IamAuthorities.DEPLOYMENT_VIEW); }
    public void requireDeploymentManage(UUID projectId) { require(projectId, IamAuthorities.DEPLOYMENT_MANAGE); }
    private void require(UUID projectId, IamPermissionAction a) {
        try { projectAuthorization.requireProjectPermission(projectId, a); }
        catch (RuntimeException ex) { throw QualityExceptions.accessDenied(); }
    }
}
