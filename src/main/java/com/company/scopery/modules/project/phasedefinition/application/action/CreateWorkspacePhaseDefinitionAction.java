package com.company.scopery.modules.project.phasedefinition.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.phasedefinition.application.command.CreateWorkspacePhaseDefinitionCommand;
import com.company.scopery.modules.project.phasedefinition.application.response.PhaseDefinitionResponse;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinitionRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateWorkspacePhaseDefinitionAction {

    private final PhaseDefinitionRepository repository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final ProjectActivityLogger activityLogger;

    public CreateWorkspacePhaseDefinitionAction(PhaseDefinitionRepository repository,
                                                 CurrentUserAuthorizationService currentUserAuthorizationService,
                                                 WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                                 ProjectActivityLogger activityLogger) {
        this.repository = repository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PhaseDefinitionResponse execute(CreateWorkspacePhaseDefinitionCommand command) {
        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                command.workspaceId(), actorId, IamAuthorities.PHASE_DEFINITION_CREATE);

        if (repository.existsByCodeAndScopeAndWorkspaceId(
                command.code(), PhaseDefinitionScope.WORKSPACE, command.workspaceId())) {
            throw ProjectExceptions.phaseDefinitionCodeAlreadyExists(command.code());
        }

        PhaseDefinition pd = PhaseDefinition.createWorkspace(
                command.workspaceId(),
                command.code(),
                command.name(),
                command.description(),
                command.displayOrder()
        );

        PhaseDefinition saved = repository.save(pd);

        activityLogger.logSuccess(
                ProjectEntityTypes.PHASE_DEFINITION,
                saved.id(),
                ProjectActivityActions.CREATE_WORKSPACE_PHASE_DEFINITION,
                "Workspace phase definition created: " + saved.code()
        );

        return PhaseDefinitionResponse.from(saved);
    }
}
