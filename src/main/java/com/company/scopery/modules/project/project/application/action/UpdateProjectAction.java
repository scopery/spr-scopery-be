package com.company.scopery.modules.project.project.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.application.command.UpdateProjectCommand;
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
public class UpdateProjectAction {

    private final ProjectRepository projectRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public UpdateProjectAction(ProjectRepository projectRepository,
                                ProjectActivityLogger activityLogger,
                                ProjectWorkspaceAuthorizationService authorizationService) {
        this.projectRepository = projectRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ProjectResponse execute(UpdateProjectCommand command) {
        authorizationService.requireProjectPermission(command.id(), IamAuthorities.PROJECT_UPDATE);

        Project project = projectRepository.findById(command.id())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.id()));

        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectExceptions.projectAlreadyArchived(command.id());
        }

        if (project.status() == ProjectStatus.COMPLETED) {
            throw ProjectExceptions.projectNotActiveOrDraft(command.id());
        }

        if (command.plannedStartDate() != null && command.plannedEndDate() != null
                && command.plannedEndDate().isBefore(command.plannedStartDate())) {
            throw ProjectExceptions.projectInvalidDateRange();
        }

        Project updated = project.update(
                command.name(),
                command.description(),
                command.ownerUserId(),
                command.defaultCurrency(),
                command.plannedStartDate(),
                command.plannedEndDate()
        );

        Project saved = projectRepository.save(updated);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT,
                saved.id(),
                ProjectActivityActions.UPDATE_PROJECT,
                "Project updated: " + saved.code()
        );

        return ProjectResponse.from(saved);
    }
}
