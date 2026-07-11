package com.company.scopery.modules.project.projectphase.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinitionRepository;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.application.command.CreateProjectPhaseFromDefinitionCommand;
import com.company.scopery.modules.project.projectphase.application.response.ProjectPhaseResponse;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProjectPhaseFromDefinitionAction {

    private final ProjectRepository projectRepository;
    private final PhaseDefinitionRepository phaseDefinitionRepository;
    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public CreateProjectPhaseFromDefinitionAction(ProjectRepository projectRepository,
                                                  PhaseDefinitionRepository phaseDefinitionRepository,
                                                  ProjectPhaseRepository projectPhaseRepository,
                                                  ProjectActivityLogger activityLogger,
                                                  ProjectWorkspaceAuthorizationService authorizationService) {
        this.projectRepository = projectRepository;
        this.phaseDefinitionRepository = phaseDefinitionRepository;
        this.projectPhaseRepository = projectPhaseRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ProjectPhaseResponse execute(CreateProjectPhaseFromDefinitionCommand cmd) {
        authorizationService.requireProjectPermission(cmd.projectId(), IamAuthorities.PROJECT_PHASE_CREATE);

        var project = projectRepository.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));

        if (project.status() != ProjectStatus.DRAFT && project.status() != ProjectStatus.ACTIVE) {
            throw ProjectExceptions.projectNotActiveOrDraft(cmd.projectId());
        }

        var definition = phaseDefinitionRepository.findById(cmd.phaseDefinitionId())
                .orElseThrow(() -> ProjectExceptions.phaseDefinitionNotFound(cmd.phaseDefinitionId()));

        if (definition.status() != PhaseDefinitionStatus.ACTIVE) {
            throw ProjectExceptions.phaseDefinitionNotActive(cmd.phaseDefinitionId());
        }

        if (definition.scope() == PhaseDefinitionScope.WORKSPACE
                && !definition.workspaceId().equals(project.workspaceId())) {
            throw ProjectExceptions.phaseDefinitionWorkspaceMismatch(definition.id(), project.workspaceId());
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

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_PHASE,
                saved.id(),
                ProjectActivityActions.CREATE_PROJECT_PHASE_FROM_DEFINITION,
                "Project phase created from definition: " + saved.code()
        );

        return ProjectPhaseResponse.from(saved);
    }
}
