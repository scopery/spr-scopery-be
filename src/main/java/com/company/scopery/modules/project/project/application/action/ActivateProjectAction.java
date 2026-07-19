package com.company.scopery.modules.project.project.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.application.command.ActivateProjectCommand;
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
public class ActivateProjectAction {

    private final ProjectRepository projectRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final ProjectPlatformPublisher platformPublisher;

    public ActivateProjectAction(ProjectRepository projectRepository,
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
    public ProjectResponse execute(ActivateProjectCommand command) {
        authorizationService.requireProjectActivate(command.id());

        Project project = projectRepository.findById(command.id())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.id()));

        if (project.status() != ProjectStatus.DRAFT) {
            throw ProjectExceptions.projectCannotActivate(command.id());
        }

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        Project activated = project.activate(actorId);
        Project saved = projectRepository.save(activated);

        platformPublisher.enqueueProject(saved, "PROJECT_ACTIVATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT,
                saved.id(),
                ProjectActivityActions.ACTIVATE_PROJECT,
                "Project activated: " + saved.code()
        );

        return ProjectResponse.from(saved);
    }
}
