package com.company.scopery.modules.project.projectphase.application.action;

import com.company.scopery.modules.project.projectphase.application.command.UpdateProjectPhaseCommand;
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
public class UpdateProjectPhaseAction {

    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;

    public UpdateProjectPhaseAction(ProjectPhaseRepository projectPhaseRepository,
                                    ProjectActivityLogger activityLogger,
                                    ProjectWorkspaceAuthorizationService authorizationService,
                                    ProjectMutationGuard mutationGuard,
                                    ProjectPlatformPublisher platformPublisher) {
        this.projectPhaseRepository = projectPhaseRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectPhaseResponse execute(UpdateProjectPhaseCommand cmd) {
        var phase = projectPhaseRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(cmd.id()));

        if (!phase.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.projectPhaseProjectMismatch(phase.id(), cmd.projectId());
        }

        authorizationService.requireProjectPhaseUpdate(phase.projectId());
        mutationGuard.requireMutableProject(phase.projectId());

        if (phase.status() == ProjectPhaseStatus.ARCHIVED) {
            throw ProjectExceptions.projectPhaseAlreadyArchived(cmd.id());
        }

        if (phase.status() == ProjectPhaseStatus.COMPLETED) {
            throw ProjectExceptions.projectPhaseNotActive(cmd.id());
        }

        if (cmd.plannedStartDate() != null && cmd.plannedEndDate() != null
                && cmd.plannedEndDate().isBefore(cmd.plannedStartDate())) {
            throw ProjectExceptions.projectPhaseInvalidDateRange();
        }

        var updated = phase.update(
                cmd.name(),
                cmd.description(),
                cmd.displayOrder(),
                cmd.plannedStartDate(),
                cmd.plannedEndDate()
        );

        var saved = projectPhaseRepository.save(updated);

        platformPublisher.enqueuePhase(saved, "PROJECT_PHASE_UPDATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_PHASE,
                saved.id(),
                ProjectActivityActions.UPDATE_PROJECT_PHASE,
                "Project phase updated: " + saved.code()
        );

        return ProjectPhaseResponse.from(saved);
    }
}
