package com.company.scopery.modules.project.project.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.application.command.ArchiveProjectCommand;
import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveProjectAction {

    private final ProjectRepository projectRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final ProjectPlatformPublisher platformPublisher;

    public ArchiveProjectAction(ProjectRepository projectRepository,
                                ProjectActivityLogger activityLogger,
                                ProjectWorkspaceAuthorizationService authorizationService,
                                CurrentUserAuthorizationService currentUserAuthorizationService,
                                ProjectPlatformPublisher platformPublisher) {
        this.projectRepository = projectRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResponse execute(ArchiveProjectCommand command) {
        authorizationService.requireProjectArchive(command.id());

        Project project = projectRepository.findById(command.id())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.id()));

        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectExceptions.projectAlreadyArchived(command.id());
        }

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        Project archived = project.archive(actorId);
        Project saved = projectRepository.save(archived);

        platformPublisher.enqueueProject(saved, "PROJECT_ARCHIVED");
        platformPublisher.auditProjectArchived(actorId, saved);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT,
                saved.id(),
                ProjectActivityActions.ARCHIVE_PROJECT,
                "Project archived: " + saved.code()
        );

        return ProjectResponse.from(saved);
    }
}
