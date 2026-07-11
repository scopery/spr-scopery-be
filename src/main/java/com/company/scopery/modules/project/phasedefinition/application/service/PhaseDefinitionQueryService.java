package com.company.scopery.modules.project.phasedefinition.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.phasedefinition.application.query.SearchPhaseDefinitionQuery;
import com.company.scopery.modules.project.phasedefinition.application.response.PhaseDefinitionResponse;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinitionRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.shared.constant.ProjectSortFields;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PhaseDefinitionQueryService {

    private final PhaseDefinitionRepository repository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;

    public PhaseDefinitionQueryService(PhaseDefinitionRepository repository,
                                        CurrentUserAuthorizationService currentUserAuthorizationService,
                                        WorkspaceIamIntegrationService workspaceIamIntegrationService) {
        this.repository = repository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
    }

    @Transactional(readOnly = true)
    public PhaseDefinitionResponse getPhaseDefinition(UUID id) {
        PhaseDefinition definition = repository.findById(id)
                .orElseThrow(() -> ProjectExceptions.phaseDefinitionNotFound(id));
        if (definition.scope() == PhaseDefinitionScope.WORKSPACE) {
            requireWorkspaceView(definition.workspaceId());
        }
        return PhaseDefinitionResponse.from(definition);
    }

    @Transactional(readOnly = true)
    public PageResult<PhaseDefinitionResponse> searchPhaseDefinitions(SearchPhaseDefinitionQuery query) {
        PhaseDefinitionScope scope = ProjectEnumParser.parseOptional(
                PhaseDefinitionScope.class, query.scope(), "INVALID_SCOPE", "scope");
        PhaseDefinitionStatus status = ProjectEnumParser.parseOptional(
                PhaseDefinitionStatus.class, query.status(), "INVALID_STATUS", "status");

        PageQuery pageQuery = PageQuery.of(
                query.page(),
                query.size(),
                ProjectSortFields.DISPLAY_ORDER,
                false
        );

        UUID workspaceId = query.workspaceId();
        if (workspaceId != null) {
            requireWorkspaceView(workspaceId);
        } else {
            // Without a workspace context, only the shared system catalog is browsable —
            // otherwise this would enumerate every workspace's phase definitions at once.
            scope = PhaseDefinitionScope.SYSTEM;
        }

        return repository.search(scope, workspaceId, query.keyword(), status, pageQuery)
                .map(PhaseDefinitionResponse::from);
    }

    private void requireWorkspaceView(UUID workspaceId) {
        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                workspaceId, actorId, IamAuthorities.PHASE_DEFINITION_VIEW);
    }
}
