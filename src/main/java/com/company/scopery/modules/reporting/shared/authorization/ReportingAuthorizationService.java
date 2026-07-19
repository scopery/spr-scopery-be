package com.company.scopery.modules.reporting.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class ReportingAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public ReportingAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }
    public void requireDashboard(UUID projectId) { require(projectId, IamAuthorities.REPORTING_DASHBOARD_VIEW); }
    public void requireReportView(UUID projectId) { require(projectId, IamAuthorities.REPORTING_REPORT_VIEW); }
    public void requireReportRun(UUID projectId) { require(projectId, IamAuthorities.REPORTING_REPORT_RUN); }
    public void requireExport(UUID projectId) { require(projectId, IamAuthorities.REPORTING_REPORT_EXPORT); }
    public boolean canViewFinance(UUID projectId) {
        try { projectAuthorization.requireProjectPermission(projectId, IamAuthorities.PROJECT_FINANCE_VIEW); return true; }
        catch (RuntimeException ex) { return false; }
    }
    public boolean canViewQuote(UUID projectId) {
        try { projectAuthorization.requireProjectPermission(projectId, IamAuthorities.QUOTE_VIEW); return true; }
        catch (RuntimeException ex) { return false; }
    }
    private void require(UUID projectId, IamPermissionAction authority) {
        try { projectAuthorization.requireProjectPermission(projectId, authority); }
        catch (RuntimeException ex) { throw ReportingExceptions.accessDenied(); }
    }
}
