package com.company.scopery.modules.aiplanning.shared.authorization;

import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiPlanningAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;

    public AiPlanningAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }

    public void requireView(UUID projectId) { require(projectId, IamAuthorities.AI_PROJECT_PLANNING_VIEW); }
    public void requireRun(UUID projectId) { require(projectId, IamAuthorities.AI_PROJECT_PLANNING_RUN); }
    public void requireReview(UUID projectId) { require(projectId, IamAuthorities.AI_PROJECT_PLANNING_REVIEW); }
    public void requireAccept(UUID projectId) { require(projectId, IamAuthorities.AI_PROJECT_PLANNING_ACCEPT); }
    public void requireReject(UUID projectId) { require(projectId, IamAuthorities.AI_PROJECT_PLANNING_REJECT); }
    public void requireApply(UUID projectId) { require(projectId, IamAuthorities.AI_PROJECT_PLANNING_APPLY); }
    public void requireArchive(UUID projectId) { require(projectId, IamAuthorities.AI_PROJECT_PLANNING_ARCHIVE); }
    public void requirePlanDraft(UUID projectId) { require(projectId, IamAuthorities.AI_PROJECT_PLAN_DRAFT); }
    public void requireFinanceInsight(UUID projectId) { require(projectId, IamAuthorities.AI_FINANCE_INSIGHT); }
    public void requireQuoteDraft(UUID projectId) { require(projectId, IamAuthorities.AI_QUOTE_DRAFT); }

    public boolean canViewFinance(UUID projectId) {
        try {
            projectAuthorization.requireProjectPermission(projectId, IamAuthorities.PROJECT_FINANCE_VIEW);
            return true;
        } catch (RuntimeException ex) { return false; }
    }

    public boolean canViewQuote(UUID projectId) {
        try {
            projectAuthorization.requireProjectPermission(projectId, IamAuthorities.QUOTE_VIEW);
            return true;
        } catch (RuntimeException ex) { return false; }
    }

    private void require(UUID projectId, IamPermissionAction authority) {
        try {
            projectAuthorization.requireProjectPermission(projectId, authority);
        } catch (RuntimeException ex) {
            throw AiPlanningExceptions.accessDenied();
        }
    }
}
