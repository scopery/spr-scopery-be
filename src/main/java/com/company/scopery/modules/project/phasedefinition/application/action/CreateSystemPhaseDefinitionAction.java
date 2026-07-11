package com.company.scopery.modules.project.phasedefinition.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.phasedefinition.application.command.CreateSystemPhaseDefinitionCommand;
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

@Component
public class CreateSystemPhaseDefinitionAction {

    private final PhaseDefinitionRepository repository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final ProjectActivityLogger activityLogger;

    public CreateSystemPhaseDefinitionAction(PhaseDefinitionRepository repository,
                                              IamSystemAuthorizationService systemAuthorizationService,
                                              ProjectActivityLogger activityLogger) {
        this.repository = repository;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PhaseDefinitionResponse execute(CreateSystemPhaseDefinitionCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_PHASE_DEFINITION.legacyRightCode());

        if (repository.existsByCodeAndScope(command.code(), PhaseDefinitionScope.SYSTEM)) {
            throw ProjectExceptions.phaseDefinitionCodeAlreadyExists(command.code());
        }

        PhaseDefinition pd = PhaseDefinition.createSystem(
                command.code(),
                command.name(),
                command.description(),
                command.displayOrder(),
                command.isSystemDefault()
        );

        PhaseDefinition saved = repository.save(pd);

        activityLogger.logSuccess(
                ProjectEntityTypes.PHASE_DEFINITION,
                saved.id(),
                ProjectActivityActions.CREATE_SYSTEM_PHASE_DEFINITION,
                "System phase definition created: " + saved.code()
        );

        return PhaseDefinitionResponse.from(saved);
    }
}
