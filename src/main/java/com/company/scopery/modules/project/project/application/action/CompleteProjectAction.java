package com.company.scopery.modules.project.project.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.application.command.CompleteProjectCommand;
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
public class CompleteProjectAction {

    private final ProjectRepository projectRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public CompleteProjectAction(ProjectRepository projectRepository,
                                  ProjectActivityLogger activityLogger,
                                  ProjectWorkspaceAuthorizationService authorizationService) {
        this.projectRepository = projectRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ProjectResponse execute(CompleteProjectCommand command) {
        authorizationService.requireProjectPermission(command.id(), IamAuthorities.PROJECT_UPDATE);

        Project project = projectRepository.findById(command.id())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.id()));

        if (project.status() != ProjectStatus.ACTIVE) {
            throw ProjectExceptions.projectCannotComplete(command.id());
        }

        Project completed = project.complete();
        Project saved = projectRepository.save(completed);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT,
                saved.id(),
                ProjectActivityActions.COMPLETE_PROJECT,
                "Project completed: " + saved.code()
        );

        return ProjectResponse.from(saved);
    }
}
