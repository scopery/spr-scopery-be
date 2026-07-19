package com.company.scopery.modules.externalparty.relationship.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.externalparty.relationship.application.command.CreateProjectExternalPartyRelationshipCommand;
import com.company.scopery.modules.externalparty.relationship.application.response.ProjectExternalPartyRelationshipResponse;
import com.company.scopery.modules.externalparty.relationship.domain.model.ProjectExternalPartyRelationship;
import com.company.scopery.modules.externalparty.relationship.domain.model.ProjectExternalPartyRelationshipRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateProjectExternalPartyRelationshipAction {
    private final ProjectRepository projects;
    private final ProjectExternalPartyRelationshipRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    public CreateProjectExternalPartyRelationshipAction(ProjectRepository projects, ProjectExternalPartyRelationshipRepository repo, ExternalPartyAuthorizationService authorization) {
        this.projects=projects; this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public ProjectExternalPartyRelationshipResponse execute(CreateProjectExternalPartyRelationshipCommand c) {
        authorization.requireCreate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw ExternalPartyExceptions.projectArchived(c.projectId());
        return ProjectExternalPartyRelationshipResponse.from(repo.save(ProjectExternalPartyRelationship.create(project.id(), project.workspaceId(), c.organizationId(), c.relationshipType().trim(), c.notes())));
    }
}
