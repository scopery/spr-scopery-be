package com.company.scopery.modules.externalparty.authority.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.externalparty.authority.application.command.CreateProjectApprovalAuthorityCommand;
import com.company.scopery.modules.externalparty.authority.application.response.ProjectApprovalAuthorityResponse;
import com.company.scopery.modules.externalparty.authority.domain.model.ProjectApprovalAuthority;
import com.company.scopery.modules.externalparty.authority.domain.model.ProjectApprovalAuthorityRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateProjectApprovalAuthorityAction {
    private final ProjectRepository projects;
    private final ProjectApprovalAuthorityRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    public CreateProjectApprovalAuthorityAction(ProjectRepository projects, ProjectApprovalAuthorityRepository repo, ExternalPartyAuthorizationService authorization) {
        this.projects=projects; this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public ProjectApprovalAuthorityResponse execute(CreateProjectApprovalAuthorityCommand c) {
        authorization.requireCreate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw ExternalPartyExceptions.projectArchived(c.projectId());
        return ProjectApprovalAuthorityResponse.from(repo.save(ProjectApprovalAuthority.create(project.id(), c.stakeholderId(), c.authorityType().trim(), c.notes())));
    }
}
