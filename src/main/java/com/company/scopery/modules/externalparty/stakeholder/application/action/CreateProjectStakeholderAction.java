package com.company.scopery.modules.externalparty.stakeholder.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.externalparty.shared.activity.ExternalPartyActivityLogger; import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.constant.*; import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import com.company.scopery.modules.externalparty.stakeholder.application.command.CreateProjectStakeholderCommand; import com.company.scopery.modules.externalparty.stakeholder.application.response.ProjectStakeholderResponse;
import com.company.scopery.modules.externalparty.stakeholder.domain.model.*;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateProjectStakeholderAction {
    private final ProjectRepository projects; private final ProjectStakeholderRepository repo;
    private final ExternalPartyAuthorizationService authorization; private final ExternalPartyActivityLogger activityLogger;
    public CreateProjectStakeholderAction(ProjectRepository projects, ProjectStakeholderRepository repo, ExternalPartyAuthorizationService authorization, ExternalPartyActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public ProjectStakeholderResponse execute(CreateProjectStakeholderCommand c) {
        authorization.requireCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw ExternalPartyExceptions.projectArchived(c.projectId());
        var saved = repo.save(ProjectStakeholder.create(project.id(), project.workspaceId(), c.contactId(), c.organizationId(),
                c.internalUserId(), c.stakeholderRole().trim(), c.clientFacing()));
        activityLogger.logSuccess(ExternalPartyEntityTypes.STAKEHOLDER, saved.id(), ExternalPartyActivityActions.STAKEHOLDER_CREATED, "Stakeholder created");
        return ProjectStakeholderResponse.from(saved);
    }
}
