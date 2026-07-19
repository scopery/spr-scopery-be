package com.company.scopery.modules.project.project.application.action;

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
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
public class UpdateProjectAction {

    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectPlatformPublisher platformPublisher;

    public UpdateProjectAction(ProjectRepository projectRepository,
                               WorkspaceMemberRepository workspaceMemberRepository,
                               ProjectActivityLogger activityLogger,
                               ProjectWorkspaceAuthorizationService authorizationService,
                               ProjectPlatformPublisher platformPublisher) {
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResponse execute(UpdateProjectCommand command) {
        authorizationService.requireProjectUpdate(command.id());

        Project project = projectRepository.findById(command.id())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.id()));

        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectExceptions.projectAlreadyArchived(command.id());
        }

        if (project.status() == ProjectStatus.COMPLETED) {
            throw ProjectExceptions.projectNotActiveOrDraft(command.id());
        }

        if (command.ownerUserId() != null && !Objects.equals(command.ownerUserId(), project.ownerUserId())
                && !workspaceMemberRepository.isActiveMember(project.workspaceId(), command.ownerUserId())) {
            throw ProjectExceptions.projectOwnerNotWorkspaceMember(command.ownerUserId());
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

        platformPublisher.enqueueProject(saved, "PROJECT_UPDATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT,
                saved.id(),
                ProjectActivityActions.UPDATE_PROJECT,
                "Project updated: " + saved.code()
        );

        return ProjectResponse.from(saved);
    }
}
