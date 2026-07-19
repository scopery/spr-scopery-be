package com.company.scopery.modules.clientportal.grant.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.clientportal.grant.application.command.CreatePortalAccessGrantCommand;
import com.company.scopery.modules.clientportal.grant.application.response.ExternalProjectAccessGrantResponse;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrant;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrantRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreatePortalAccessGrantAction {
    private final ProjectRepository projects;
    private final ExternalProjectAccessGrantRepository repo;
    private final ClientPortalAuthorizationService authorization;
    private final ClientPortalActivityLogger activityLogger;
    public CreatePortalAccessGrantAction(ProjectRepository projects, ExternalProjectAccessGrantRepository repo,
            ClientPortalAuthorizationService authorization, ClientPortalActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public ExternalProjectAccessGrantResponse execute(CreatePortalAccessGrantCommand c) {
        authorization.requireManage(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw ClientPortalExceptions.projectArchived(c.projectId());
        var saved = repo.save(ExternalProjectAccessGrant.create(project.id(), project.workspaceId(), c.portalAccountId(), c.permissionPolicyCode(), c.expiresAt()));
        activityLogger.logSuccess(ClientPortalEntityTypes.ACCESS_GRANT, saved.id(), ClientPortalActivityActions.GRANT_CREATED, "Portal grant created");
        return ExternalProjectAccessGrantResponse.from(saved);
    }
}
