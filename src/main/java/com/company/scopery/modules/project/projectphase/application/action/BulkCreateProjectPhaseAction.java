package com.company.scopery.modules.project.projectphase.application.action;

import com.company.scopery.modules.project.projectphase.application.command.BulkCreateProjectPhaseCommand;
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

import java.util.ArrayList;
import java.util.List;

@Component
public class BulkCreateProjectPhaseAction {

    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;

    public BulkCreateProjectPhaseAction(ProjectPhaseRepository projectPhaseRepository,
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
    public List<ProjectPhaseResponse> execute(BulkCreateProjectPhaseCommand cmd) {
        authorizationService.requireProjectPhaseCreate(cmd.projectId());
        mutationGuard.requireMutableProject(cmd.projectId());

        List<ProjectPhaseResponse> results = new ArrayList<>();

        for (CreateProjectPhaseCommand item : cmd.items()) {
            if (item.plannedStartDate() != null && item.plannedEndDate() != null
                    && item.plannedEndDate().isBefore(item.plannedStartDate())) {
                throw ProjectExceptions.projectPhaseInvalidDateRange();
            }

            if (projectPhaseRepository.existsByProjectIdAndCode(cmd.projectId(), item.code())) {
                throw ProjectExceptions.projectPhaseCodeAlreadyExists(item.code(), cmd.projectId());
            }

            if (projectPhaseRepository.existsByProjectIdAndDisplayOrder(cmd.projectId(), item.displayOrder())) {
                throw ProjectExceptions.projectPhaseDisplayOrderConflict(item.displayOrder(), cmd.projectId());
            }

            ProjectPhase phase = ProjectPhase.create(
                    cmd.projectId(),
                    item.code(),
                    item.name(),
                    item.description(),
                    item.displayOrder(),
                    item.plannedStartDate(),
                    item.plannedEndDate()
            );

            ProjectPhase saved = projectPhaseRepository.save(phase);
            platformPublisher.enqueuePhase(saved, "PROJECT_PHASE_CREATED");

            activityLogger.logSuccess(
                    ProjectEntityTypes.PROJECT_PHASE,
                    saved.id(),
                    ProjectActivityActions.CREATE_PROJECT_PHASE,
                    "Project phase created: " + saved.code()
            );

            results.add(ProjectPhaseResponse.from(saved));
        }

        return results;
    }
}
