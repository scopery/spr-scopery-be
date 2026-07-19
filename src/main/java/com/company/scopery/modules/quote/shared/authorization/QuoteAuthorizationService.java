package com.company.scopery.modules.quote.shared.authorization;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QuoteAuthorizationService {

    private final ProjectWorkspaceAuthorizationService projectAuthorization;

    public QuoteAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }

    public void requireView(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_VIEW);
    }

    public void requireCreate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_CREATE);
    }

    public void requireUpdate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_UPDATE);
    }

    public void requireArchive(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_ARCHIVE);
    }

    public void requireVersionCreate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_VERSION_CREATE);
    }

    public void requireVersionUpdate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_VERSION_UPDATE);
    }

    public void requireVersionArchive(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_VERSION_ARCHIVE);
    }

    public void requireVersionDuplicate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_VERSION_DUPLICATE);
    }

    public void requireLineCreate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_LINE_CREATE);
    }

    public void requireLineUpdate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_LINE_UPDATE);
    }

    public void requireLineDelete(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_LINE_DELETE);
    }

    public void requireTermCreate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_TERM_CREATE);
    }

    public void requireTermUpdate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_TERM_UPDATE);
    }

    public void requireTermDelete(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_TERM_DELETE);
    }

    public void requireSolverUse(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_SOLVER_USE);
    }

    public void requireRecalculate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_RECALCULATE);
    }

    public void requireSubmit(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_SUBMIT);
    }

    public void requireApprove(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_APPROVE);
    }

    public void requireReject(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_REJECT);
    }

    public void requireSend(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_SEND);
    }

    public void requireMarkAccepted(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_MARK_ACCEPTED);
    }

    public void requireMarkCurrent(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_MARK_CURRENT);
    }

    public void requireDiscountUpdate(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_DISCOUNT_UPDATE);
    }

    public void requireDiscountApprove(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_DISCOUNT_APPROVE);
    }

    public boolean canViewMargin(UUID projectId) {
        try {
            projectAuthorization.requireProjectPermission(projectId, IamAuthorities.QUOTE_MARGIN_VIEW);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public void requireMarginView(UUID projectId) {
        require(projectId, IamAuthorities.QUOTE_MARGIN_VIEW);
    }

    private void require(UUID projectId, IamPermissionAction authority) {
        try {
            projectAuthorization.requireProjectPermission(projectId, authority);
        } catch (RuntimeException ex) {
            throw QuoteExceptions.accessDenied();
        }
    }
}
