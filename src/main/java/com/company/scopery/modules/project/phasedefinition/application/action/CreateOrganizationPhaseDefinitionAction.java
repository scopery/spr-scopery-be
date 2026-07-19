package com.company.scopery.modules.project.phasedefinition.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.phasedefinition.application.command.CreateOrganizationPhaseDefinitionCommand;
import com.company.scopery.modules.project.phasedefinition.application.response.PhaseDefinitionResponse;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinitionRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateOrganizationPhaseDefinitionAction {

    private final PhaseDefinitionRepository repository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public CreateOrganizationPhaseDefinitionAction(PhaseDefinitionRepository repository,
                                                    IamSystemAuthorizationService systemAuthorizationService,
                                                    ProjectActivityLogger activityLogger,
                                                    ProjectPlatformPublisher platformPublisher) {
        this.repository = repository;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public PhaseDefinitionResponse execute(CreateOrganizationPhaseDefinitionCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_PHASE_DEFINITION.legacyRightCode());

        if (command.organizationId() == null) {
            throw ProjectExceptions.phaseDefinitionInvalidScope("organizationId is required for ORGANIZATION scope");
        }

        if (repository.existsByCodeAndScopeAndOrganizationId(
                command.code(), PhaseDefinitionScope.ORGANIZATION, command.organizationId())) {
            throw ProjectExceptions.phaseDefinitionCodeAlreadyExists(command.code());
        }

        PhaseDefinition pd = PhaseDefinition.createOrganization(
                command.organizationId(),
                command.code(),
                command.name(),
                command.description(),
                command.displayOrder()
        );

        PhaseDefinition saved = repository.save(pd);

        platformPublisher.enqueuePhaseDefinition(saved, "PHASE_DEFINITION_CREATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.PHASE_DEFINITION,
                saved.id(),
                ProjectActivityActions.CREATE_ORGANIZATION_PHASE_DEFINITION,
                "Organization phase definition created: " + saved.code()
        );

        return PhaseDefinitionResponse.from(saved);
    }
}
