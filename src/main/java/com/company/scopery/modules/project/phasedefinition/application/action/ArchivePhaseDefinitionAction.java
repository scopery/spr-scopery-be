package com.company.scopery.modules.project.phasedefinition.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.phasedefinition.application.command.ArchivePhaseDefinitionCommand;
import com.company.scopery.modules.project.phasedefinition.application.response.PhaseDefinitionResponse;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;
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
public class ArchivePhaseDefinitionAction {

    private final PhaseDefinitionRepository repository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final ProjectActivityLogger activityLogger;

    public ArchivePhaseDefinitionAction(PhaseDefinitionRepository repository,
                                         CurrentUserAuthorizationService currentUserAuthorizationService,
                                         WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                         IamSystemAuthorizationService systemAuthorizationService,
                                         ProjectActivityLogger activityLogger) {
        this.repository = repository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PhaseDefinitionResponse execute(ArchivePhaseDefinitionCommand command) {
        PhaseDefinition pd = repository.findById(command.id())
                .orElseThrow(() -> ProjectExceptions.phaseDefinitionNotFound(command.id()));

        if (pd.status() == PhaseDefinitionStatus.ARCHIVED) {
            throw ProjectExceptions.phaseDefinitionAlreadyArchived(command.id());
        }

        if (pd.scope() == PhaseDefinitionScope.SYSTEM) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_PHASE_DEFINITION.legacyRightCode());
        } else {
            UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    pd.workspaceId(), actorId, IamAuthorities.PHASE_DEFINITION_ARCHIVE);
        }

        if (repository.isUsedByAnyProject(command.id())) {
            throw ProjectExceptions.phaseDefinitionInUse(command.id());
        }

        PhaseDefinition archived = pd.archive();
        PhaseDefinition saved = repository.save(archived);

        activityLogger.logSuccess(
                ProjectEntityTypes.PHASE_DEFINITION,
                saved.id(),
                ProjectActivityActions.ARCHIVE_PHASE_DEFINITION,
                "Phase definition archived: " + saved.code()
        );

        return PhaseDefinitionResponse.from(saved);
    }
}
