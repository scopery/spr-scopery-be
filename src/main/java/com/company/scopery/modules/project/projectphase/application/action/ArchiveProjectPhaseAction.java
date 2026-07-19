package com.company.scopery.modules.project.projectphase.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.projectphase.application.command.ArchiveProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.application.response.ProjectPhaseResponse;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveProjectPhaseAction {

    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public ArchiveProjectPhaseAction(ProjectPhaseRepository projectPhaseRepository,
                                     ProjectActivityLogger activityLogger,
                                     ProjectWorkspaceAuthorizationService authorizationService,
                                     ProjectMutationGuard mutationGuard,
                                     ProjectPlatformPublisher platformPublisher,
                                     CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.projectPhaseRepository = projectPhaseRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional
    public ProjectPhaseResponse execute(ArchiveProjectPhaseCommand cmd) {
        var phase = projectPhaseRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(cmd.id()));

        if (!phase.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.projectPhaseProjectMismatch(phase.id(), cmd.projectId());
        }

        authorizationService.requireProjectPhaseArchive(phase.projectId());
        Project project = mutationGuard.requireMutableProject(phase.projectId());

        if (phase.status() == ProjectPhaseStatus.ARCHIVED) {
            throw ProjectExceptions.projectPhaseAlreadyArchived(cmd.id());
        }

        if (projectPhaseRepository.hasActiveWbsNodesOrTasks(cmd.id())) {
            throw ProjectExceptions.projectPhaseCannotArchive(cmd.id());
        }

        var archived = phase.archive();
        var saved = projectPhaseRepository.save(archived);

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        platformPublisher.enqueuePhase(saved, "PROJECT_PHASE_ARCHIVED");
        platformPublisher.auditPhaseArchived(actorId, saved, project.organizationId(), project.workspaceId());

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_PHASE,
                saved.id(),
                ProjectActivityActions.ARCHIVE_PROJECT_PHASE,
                "Project phase archived: " + saved.code()
        );

        return ProjectPhaseResponse.from(saved);
    }
}
