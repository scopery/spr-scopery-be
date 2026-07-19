package com.company.scopery.modules.estimation.shared.authorization;

import com.company.scopery.modules.estimation.shared.error.EstimationExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EstimationAuthorizationService {

    private final ProjectWorkspaceAuthorizationService projectAuthorization;

    public EstimationAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }

    public void requireView(UUID projectId) {
        require(projectId, IamAuthorities.ESTIMATION_VIEW);
    }

    public void requireCreate(UUID projectId) {
        require(projectId, IamAuthorities.ESTIMATION_RUN_CREATE);
    }

    public void requireCancel(UUID projectId) {
        require(projectId, IamAuthorities.ESTIMATION_RUN_CANCEL);
    }

    public void requireMarkCurrent(UUID projectId) {
        require(projectId, IamAuthorities.ESTIMATION_MARK_CURRENT);
    }

    public void requireTaskView(UUID projectId) {
        require(projectId, IamAuthorities.ESTIMATION_TASK_VIEW);
    }

    public void requireWbsView(UUID projectId) {
        require(projectId, IamAuthorities.ESTIMATION_WBS_VIEW);
    }

    public void requirePhaseView(UUID projectId) {
        require(projectId, IamAuthorities.ESTIMATION_PHASE_VIEW);
    }

    public void requireSummaryView(UUID projectId) {
        require(projectId, IamAuthorities.ESTIMATION_SUMMARY_VIEW);
    }

    public boolean hasRateDetailView(UUID projectId) {
        try {
            require(projectId, IamAuthorities.ESTIMATION_RATE_DETAIL_VIEW);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private void require(UUID projectId, IamPermissionAction authority) {
        try {
            projectAuthorization.requireProjectPermission(projectId, authority);
        } catch (RuntimeException ex) {
            throw EstimationExceptions.accessDenied();
        }
    }
}
