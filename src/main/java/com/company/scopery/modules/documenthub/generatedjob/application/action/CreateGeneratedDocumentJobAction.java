package com.company.scopery.modules.documenthub.generatedjob.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.documenthub.generatedjob.application.command.CreateGeneratedDocumentJobCommand;
import com.company.scopery.modules.documenthub.generatedjob.application.response.GeneratedDocumentJobResponse;
import com.company.scopery.modules.documenthub.generatedjob.domain.model.GeneratedDocumentJob;
import com.company.scopery.modules.documenthub.generatedjob.domain.model.GeneratedDocumentJobRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateGeneratedDocumentJobAction {
    private final ProjectRepository projects;
    private final GeneratedDocumentJobRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final DocumentHubActivityLogger activityLogger;
    public CreateGeneratedDocumentJobAction(ProjectRepository projects, GeneratedDocumentJobRepository repo,
            DocumentHubAuthorizationService authorization, CurrentUserAuthorizationService currentUser, DocumentHubActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public GeneratedDocumentJobResponse execute(CreateGeneratedDocumentJobCommand c) {
        authorization.requireCreate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw DocumentHubExceptions.projectArchived(c.projectId());
        var saved = repo.save(GeneratedDocumentJob.create(project.workspaceId(), project.id(), c.templateId(), c.templateVersionId(),
                c.jobType().trim(), c.sourceType(), c.sourceId(), currentUser.resolveCurrentUser().id()));
        activityLogger.logSuccess(DocumentHubEntityTypes.GENERATED_JOB, saved.id(), DocumentHubActivityActions.GENERATION_REQUESTED, "Generation job queued");
        return GeneratedDocumentJobResponse.from(saved);
    }
}
