package com.company.scopery.modules.ratecard.costrole.application.action;

import com.company.scopery.modules.ratecard.costrole.application.command.CreateCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.application.response.CostRoleResponse;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import com.company.scopery.modules.ratecard.shared.util.RateCardEnumParser;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateCostRoleAction {

    private final CostRoleRepository costRoleRepository;
    private final WorkspaceRepository workspaceRepository;
    private final OrganizationRepository organizationRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public CreateCostRoleAction(CostRoleRepository costRoleRepository,
                                WorkspaceRepository workspaceRepository,
                                OrganizationRepository organizationRepository,
                                RateCardAuthorizationService authorizationService,
                                RateCardActivityLogger activityLogger,
                                RateCardPlatformPublisher platformPublisher) {
        this.costRoleRepository = costRoleRepository;
        this.workspaceRepository = workspaceRepository;
        this.organizationRepository = organizationRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public CostRoleResponse execute(CreateCostRoleCommand cmd) {
        CostRoleScope scope = RateCardEnumParser.parseRequired(
                CostRoleScope.class, cmd.scope(), "COST_ROLE_INVALID_SCOPE", "scope");

        authorizationService.requireCostRoleCreate(cmd.workspaceId());

        final UUID organizationId;
        final UUID workspaceId;
        if (scope == CostRoleScope.SYSTEM) {
            organizationId = null;
            workspaceId = null;
        } else if (scope == CostRoleScope.ORGANIZATION) {
            if (cmd.organizationId() == null) {
                throw RateCardExceptions.costRoleInvalidScope(scope.name());
            }
            UUID orgId = cmd.organizationId();
            var org = organizationRepository.findById(orgId)
                    .orElseThrow(() -> RateCardExceptions.organizationNotFound(orgId));
            if (org.status() != OrganizationStatus.ACTIVE) {
                throw RateCardExceptions.organizationNotActive(orgId);
            }
            organizationId = orgId;
            workspaceId = null;
        } else if (scope == CostRoleScope.WORKSPACE) {
            if (cmd.workspaceId() == null) {
                throw RateCardExceptions.costRoleInvalidScope(scope.name());
            }
            UUID wsId = cmd.workspaceId();
            var ws = workspaceRepository.findById(wsId)
                    .orElseThrow(() -> RateCardExceptions.workspaceNotFound(wsId));
            if (ws.status() != WorkspaceStatus.ACTIVE) {
                throw RateCardExceptions.workspaceNotActive(wsId);
            }
            organizationId = ws.organizationId();
            workspaceId = wsId;
        } else {
            throw RateCardExceptions.costRoleInvalidScope(scope.name());
        }

        String code = CostRole.normalizeCode(cmd.code());
        if (costRoleRepository.existsByScopeAndCode(scope, organizationId, workspaceId, code)) {
            throw RateCardExceptions.costRoleCodeAlreadyExists(code);
        }

        CostRole saved = costRoleRepository.save(
                CostRole.create(code, cmd.name(), cmd.description(), scope, organizationId, workspaceId, cmd.category(), false));

        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_COST_ROLE, saved.id(),
                "COST_ROLE_CREATED", RateCardPlatformPublisher.mapOf(
                        "id", saved.id(), "code", saved.code(), "scope", saved.scope().name()));
        activityLogger.logSuccess(RateCardEntityTypes.COST_ROLE, saved.id(),
                RateCardActivityActions.COST_ROLE_CREATED, "Cost role created: " + saved.code());
        return CostRoleResponse.from(saved);
    }
}
