package com.company.scopery.modules.project.project.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.application.command.CreateProjectCommand;
import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.project.domain.valueobject.ProjectCode;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProjectAction {

    private final ProjectRepository projectRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public CreateProjectAction(ProjectRepository projectRepository,
                                ProjectActivityLogger activityLogger,
                                ProjectWorkspaceAuthorizationService authorizationService) {
        this.projectRepository = projectRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ProjectResponse execute(CreateProjectCommand command) {
        authorizationService.requireWorkspacePermission(command.workspaceId(), IamAuthorities.PROJECT_CREATE);

        ProjectCode code = ProjectCode.of(command.code());

        if (projectRepository.existsByWorkspaceIdAndCode(command.workspaceId(), code.value())) {
            throw ProjectExceptions.projectCodeAlreadyExists(code.value());
        }

        if (command.plannedStartDate() != null && command.plannedEndDate() != null
                && command.plannedEndDate().isBefore(command.plannedStartDate())) {
            throw ProjectExceptions.projectInvalidDateRange();
        }

        Project project = Project.create(
                command.workspaceId(),
                code.value(),
                command.name(),
                command.description(),
                command.ownerUserId(),
                command.defaultCurrency(),
                command.plannedStartDate(),
                command.plannedEndDate()
        );

        Project saved = projectRepository.save(project);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT,
                saved.id(),
                ProjectActivityActions.CREATE_PROJECT,
                "Project created: " + saved.code()
        );

        return ProjectResponse.from(saved);
    }
}
