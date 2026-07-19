package com.company.scopery.modules.projectbaseline.shared.authorization;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectBaselineAuthorizationService {

    private final ProjectWorkspaceAuthorizationService projectAuthorization;

    public ProjectBaselineAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }

    public void requireBaselineView(UUID projectId) { requireBaseline(projectId, IamAuthorities.PROJECT_BASELINE_VIEW); }
    public void requireBaselineCreate(UUID projectId) { requireBaseline(projectId, IamAuthorities.PROJECT_BASELINE_CREATE); }
    public void requireBaselineUpdate(UUID projectId) { requireBaseline(projectId, IamAuthorities.PROJECT_BASELINE_UPDATE); }
    public void requireBaselineValidate(UUID projectId) { requireBaseline(projectId, IamAuthorities.PROJECT_BASELINE_VALIDATE); }
    public void requireBaselineApprove(UUID projectId) { requireBaseline(projectId, IamAuthorities.PROJECT_BASELINE_APPROVE); }
    public void requireBaselineMarkCurrent(UUID projectId) { requireBaseline(projectId, IamAuthorities.PROJECT_BASELINE_MARK_CURRENT); }
    public void requireBaselineArchive(UUID projectId) { requireBaseline(projectId, IamAuthorities.PROJECT_BASELINE_ARCHIVE); }

    public void requireChangeRequestView(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_VIEW); }
    public void requireChangeRequestCreate(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_CREATE); }
    public void requireChangeRequestUpdate(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_UPDATE); }
    public void requireChangeRequestSubmit(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_SUBMIT); }
    public void requireChangeRequestApprove(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_APPROVE); }
    public void requireChangeRequestReject(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_REJECT); }
    public void requireChangeRequestCancel(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_CANCEL); }
    public void requireChangeRequestApply(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_APPLY); }
    public void requireChangeRequestArchive(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_ARCHIVE); }

    public void requireItemView(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_ITEM_VIEW); }
    public void requireItemCreate(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_ITEM_CREATE); }
    public void requireItemUpdate(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_ITEM_UPDATE); }
    public void requireItemDelete(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_REQUEST_ITEM_DELETE); }

    public void requireImpactView(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_IMPACT_VIEW); }
    public void requireImpactUpdate(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_IMPACT_UPDATE); }
    public void requireImpactCalculate(UUID projectId) { requireCr(projectId, IamAuthorities.CHANGE_IMPACT_CALCULATE); }

    public void requireChangeOrderView(UUID projectId) { requireCo(projectId, IamAuthorities.CHANGE_ORDER_VIEW); }
    public void requireChangeOrderCreate(UUID projectId) { requireCo(projectId, IamAuthorities.CHANGE_ORDER_CREATE); }
    public void requireChangeOrderUpdate(UUID projectId) { requireCo(projectId, IamAuthorities.CHANGE_ORDER_UPDATE); }
    public void requireChangeOrderApprove(UUID projectId) { requireCo(projectId, IamAuthorities.CHANGE_ORDER_APPROVE); }
    public void requireChangeOrderReject(UUID projectId) { requireCo(projectId, IamAuthorities.CHANGE_ORDER_REJECT); }
    public void requireChangeOrderArchive(UUID projectId) { requireCo(projectId, IamAuthorities.CHANGE_ORDER_ARCHIVE); }

    public boolean canViewFinanceImpact(UUID projectId) {
        try {
            projectAuthorization.requireProjectPermission(projectId, IamAuthorities.PROJECT_FINANCE_MARGIN_VIEW);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private void requireBaseline(UUID projectId, IamPermissionAction authority) {
        try {
            projectAuthorization.requireProjectPermission(projectId, authority);
        } catch (RuntimeException ex) {
            throw ProjectBaselineExceptions.baselineAccessDenied();
        }
    }

    private void requireCr(UUID projectId, IamPermissionAction authority) {
        try {
            projectAuthorization.requireProjectPermission(projectId, authority);
        } catch (RuntimeException ex) {
            throw ProjectBaselineExceptions.changeRequestAccessDenied();
        }
    }

    private void requireCo(UUID projectId, IamPermissionAction authority) {
        try {
            projectAuthorization.requireProjectPermission(projectId, authority);
        } catch (RuntimeException ex) {
            throw ProjectBaselineExceptions.changeOrderAccessDenied();
        }
    }
}
