package com.company.scopery.modules.project.project.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivateProjectAction {

    private final ProjectRepository projectRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public ActivateProjectAction(ProjectRepository projectRepository,
                                  ProjectActivityLogger activityLogger,
                                  ProjectWorkspaceAuthorizationService authorizationService) {
        this.projectRepository = projectRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ProjectResponse execute(ActivateProjectCommand command) {
        authorizationService.requireProjectPermission(command.id(), IamAuthorities.PROJECT_UPDATE);

        Project project = projectRepository.findById(command.id())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.id()));

        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectExceptions.projectAlreadyArchived(command.id());
        }

        Project activated = project.activate();
        Project saved = projectRepository.save(activated);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT,
                saved.id(),
                ProjectActivityActions.ACTIVATE_PROJECT,
                "Project activated: " + saved.code()
        );

        return ProjectResponse.from(saved);
    }
}
