package com.company.scopery.modules.project.projectphase.application.action;

import com.company.scopery.modules.project.projectphase.application.command.CreateProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.application.response.ProjectPhaseResponse;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
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
public class CreateProjectPhaseAction {

    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;

    public CreateProjectPhaseAction(ProjectPhaseRepository projectPhaseRepository,
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
    public ProjectPhaseResponse execute(CreateProjectPhaseCommand cmd) {
        authorizationService.requireProjectPhaseCreate(cmd.projectId());
        mutationGuard.requireMutableProject(cmd.projectId());

        if (cmd.plannedStartDate() != null && cmd.plannedEndDate() != null
                && cmd.plannedEndDate().isBefore(cmd.plannedStartDate())) {
            throw ProjectExceptions.projectPhaseInvalidDateRange();
        }

        if (projectPhaseRepository.existsByProjectIdAndCode(cmd.projectId(), cmd.code())) {
            throw ProjectExceptions.projectPhaseCodeAlreadyExists(cmd.code(), cmd.projectId());
        }

        if (projectPhaseRepository.existsByProjectIdAndDisplayOrder(cmd.projectId(), cmd.displayOrder())) {
            throw ProjectExceptions.projectPhaseDisplayOrderConflict(cmd.displayOrder(), cmd.projectId());
        }

        ProjectPhase phase = ProjectPhase.create(
                cmd.projectId(),
                cmd.code(),
                cmd.name(),
                cmd.description(),
                cmd.displayOrder(),
                cmd.plannedStartDate(),
                cmd.plannedEndDate()
        );

        ProjectPhase saved = projectPhaseRepository.save(phase);

        platformPublisher.enqueuePhase(saved, "PROJECT_PHASE_CREATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_PHASE,
                saved.id(),
                ProjectActivityActions.CREATE_PROJECT_PHASE,
                "Project phase created: " + saved.code()
        );

        return ProjectPhaseResponse.from(saved);
    }
}
