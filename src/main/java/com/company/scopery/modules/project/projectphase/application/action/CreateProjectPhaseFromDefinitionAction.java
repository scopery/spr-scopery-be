package com.company.scopery.modules.project.projectphase.application.action;

import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinitionRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.projectphase.application.command.CreateProjectPhaseFromDefinitionCommand;
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
public class CreateProjectPhaseFromDefinitionAction {

    private final PhaseDefinitionRepository phaseDefinitionRepository;
    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;

    public CreateProjectPhaseFromDefinitionAction(PhaseDefinitionRepository phaseDefinitionRepository,
                                                  ProjectPhaseRepository projectPhaseRepository,
                                                  ProjectActivityLogger activityLogger,
                                                  ProjectWorkspaceAuthorizationService authorizationService,
                                                  ProjectMutationGuard mutationGuard,
                                                  ProjectPlatformPublisher platformPublisher) {
        this.phaseDefinitionRepository = phaseDefinitionRepository;
        this.projectPhaseRepository = projectPhaseRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectPhaseResponse execute(CreateProjectPhaseFromDefinitionCommand cmd) {
        authorizationService.requireProjectPhaseCreate(cmd.projectId());
        Project project = mutationGuard.requireMutableProject(cmd.projectId());

        var definition = phaseDefinitionRepository.findById(cmd.phaseDefinitionId())
                .orElseThrow(() -> ProjectExceptions.phaseDefinitionNotFound(cmd.phaseDefinitionId()));

        if (definition.status() != PhaseDefinitionStatus.ACTIVE) {
            throw ProjectExceptions.phaseDefinitionNotActive(cmd.phaseDefinitionId());
        }

        if (definition.scope() == PhaseDefinitionScope.WORKSPACE
                && !definition.workspaceId().equals(project.workspaceId())) {
            throw ProjectExceptions.phaseDefinitionWorkspaceMismatch(definition.id(), project.workspaceId());
        }

        if (definition.scope() == PhaseDefinitionScope.ORGANIZATION
                && (definition.organizationId() == null
                || !definition.organizationId().equals(project.organizationId()))) {
            throw ProjectExceptions.phaseDefinitionInvalidScope(
                    "Organization phase definition does not match project organization");
        }

        if (cmd.plannedStartDate() != null && cmd.plannedEndDate() != null
                && cmd.plannedEndDate().isBefore(cmd.plannedStartDate())) {
            throw ProjectExceptions.projectPhaseInvalidDateRange();
        }

        if (projectPhaseRepository.existsByProjectIdAndDisplayOrder(cmd.projectId(), cmd.displayOrder())) {
            throw ProjectExceptions.projectPhaseDisplayOrderConflict(cmd.displayOrder(), cmd.projectId());
        }

        ProjectPhase phase = ProjectPhase.createFromDefinition(
                cmd.projectId(),
                cmd.phaseDefinitionId(),
                definition.code(),
                definition.name(),
                definition.description(),
                cmd.displayOrder(),
                cmd.plannedStartDate(),
                cmd.plannedEndDate()
        );

        ProjectPhase saved = projectPhaseRepository.save(phase);

        platformPublisher.enqueuePhase(saved, "PROJECT_PHASE_CREATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_PHASE,
                saved.id(),
                ProjectActivityActions.CREATE_PROJECT_PHASE_FROM_DEFINITION,
                "Project phase created from definition: " + saved.code()
        );

        return ProjectPhaseResponse.from(saved);
    }
}
