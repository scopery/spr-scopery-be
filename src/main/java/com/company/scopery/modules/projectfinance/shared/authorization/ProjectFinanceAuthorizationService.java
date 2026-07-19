package com.company.scopery.modules.projectfinance.shared.authorization;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectFinanceAuthorizationService {

    private final ProjectWorkspaceAuthorizationService projectAuthorization;

    public ProjectFinanceAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }

    public void requireView(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_VIEW);
    }

    public void requireCreate(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_CREATE);
    }

    public void requireUpdate(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_UPDATE);
    }

    public void requireRecalculate(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_RECALCULATE);
    }

    public void requireApprove(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_APPROVE);
    }

    public void requireMarkCurrent(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_MARK_CURRENT);
    }

    public void requireArchive(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_ARCHIVE);
    }

    public void requireCostView(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_COST_VIEW);
    }

    public void requireCostCreate(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_COST_CREATE);
    }

    public void requireCostUpdate(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_COST_UPDATE);
    }

    public void requireCostArchive(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_COST_ARCHIVE);
    }

    public void requireRevenueView(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_REVENUE_VIEW);
    }

    public void requireRevenueUpdate(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_REVENUE_UPDATE);
    }

    public void requireMarginView(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_FINANCE_MARGIN_VIEW);
    }

    private void require(UUID projectId, IamPermissionAction authority) {
        try {
            projectAuthorization.requireProjectPermission(projectId, authority);
        } catch (RuntimeException ex) {
            throw ProjectFinanceExceptions.accessDenied();
        }
    }
}
