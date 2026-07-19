package com.company.scopery.modules.project.project.application.action;

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
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProjectAction {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectPlatformPublisher platformPublisher;

    public CreateProjectAction(ProjectRepository projectRepository,
                               WorkspaceRepository workspaceRepository,
                               WorkspaceMemberRepository workspaceMemberRepository,
                               ProjectActivityLogger activityLogger,
                               ProjectWorkspaceAuthorizationService authorizationService,
                               ProjectPlatformPublisher platformPublisher) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResponse execute(CreateProjectCommand command) {
        authorizationService.requireProjectCreate(command.workspaceId());

        Workspace workspace = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> ProjectExceptions.projectWorkspaceNotFound(command.workspaceId()));

        if (workspace.status() != WorkspaceStatus.ACTIVE) {
            throw ProjectExceptions.projectWorkspaceNotActive(command.workspaceId());
        }

        if (command.ownerUserId() != null
                && !workspaceMemberRepository.isActiveMember(command.workspaceId(), command.ownerUserId())) {
            throw ProjectExceptions.projectOwnerNotWorkspaceMember(command.ownerUserId());
        }

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
                workspace.organizationId(),
                code.value(),
                command.name(),
                command.description(),
                command.ownerUserId(),
                command.defaultCurrency(),
                command.plannedStartDate(),
                command.plannedEndDate()
        );

        Project saved = projectRepository.save(project);

        platformPublisher.enqueueProject(saved, "PROJECT_CREATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT,
                saved.id(),
                ProjectActivityActions.CREATE_PROJECT,
                "Project created: " + saved.code()
        );

        return ProjectResponse.from(saved);
    }
}
