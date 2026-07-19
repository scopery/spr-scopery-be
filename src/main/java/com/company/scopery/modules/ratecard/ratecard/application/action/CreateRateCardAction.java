package com.company.scopery.modules.ratecard.ratecard.application.action;

import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.ratecard.ratecard.application.command.CreateRateCardCommand;
import com.company.scopery.modules.ratecard.ratecard.application.response.RateCardResponse;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCard;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.currency.SupportedCurrency;
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

@Component("ratecardCreateRateCardAction")
public class CreateRateCardAction {
    private final RateCardRepository rateCardRepository;
    private final WorkspaceRepository workspaceRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public CreateRateCardAction(RateCardRepository rateCardRepository, WorkspaceRepository workspaceRepository,
                                OrganizationRepository organizationRepository,
                                ProjectRepository projectRepository,
                                RateCardAuthorizationService authorizationService,
                                RateCardActivityLogger activityLogger, RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository;
        this.workspaceRepository = workspaceRepository;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public RateCardResponse execute(CreateRateCardCommand cmd) {
        RateCardScope scope = RateCardEnumParser.parseRequired(RateCardScope.class, cmd.scope(),
                "RATE_CARD_INVALID_SCOPE", "scope");
        authorizationService.requireRateCardCreate(cmd.workspaceId());
        String currency = SupportedCurrency.requireValid(cmd.defaultCurrencyCode());

        final UUID organizationId;
        final UUID workspaceId;
        final UUID clientId;
        final UUID projectId;
        if (scope == RateCardScope.SYSTEM) {
            organizationId = null;
            workspaceId = null;
            clientId = null;
            projectId = null;
        } else if (scope == RateCardScope.ORGANIZATION) {
            if (cmd.organizationId() == null) throw RateCardExceptions.rateCardInvalidScope(scope.name());
            UUID orgId = cmd.organizationId();
            var org = organizationRepository.findById(orgId)
                    .orElseThrow(() -> RateCardExceptions.organizationNotFound(orgId));
            if (org.status() != OrganizationStatus.ACTIVE) throw RateCardExceptions.organizationNotActive(orgId);
            organizationId = orgId;
            workspaceId = null;
            clientId = null;
            projectId = null;
        } else if (scope == RateCardScope.WORKSPACE) {
            if (cmd.workspaceId() == null) throw RateCardExceptions.rateCardInvalidScope(scope.name());
            UUID wsId = cmd.workspaceId();
            var ws = workspaceRepository.findById(wsId)
                    .orElseThrow(() -> RateCardExceptions.workspaceNotFound(wsId));
            if (ws.status() != WorkspaceStatus.ACTIVE) throw RateCardExceptions.workspaceNotActive(wsId);
            organizationId = ws.organizationId();
            workspaceId = wsId;
            clientId = null;
            projectId = null;
        } else if (scope == RateCardScope.CLIENT) {
            if (cmd.clientId() == null) throw RateCardExceptions.rateCardInvalidScope(scope.name());
            clientId = cmd.clientId();
            organizationId = cmd.organizationId();
            workspaceId = cmd.workspaceId();
            projectId = null;
        } else {
            if (cmd.projectId() == null) throw RateCardExceptions.rateCardInvalidScope(scope.name());
            UUID projId = cmd.projectId();
            Project project = projectRepository.findById(projId)
                    .orElseThrow(() -> ProjectExceptions.projectNotFound(projId));
            projectId = projId;
            workspaceId = project.workspaceId();
            organizationId = project.organizationId();
            clientId = cmd.clientId();
        }

        String code = RateCard.normalizeCode(cmd.code());
        if (rateCardRepository.existsByScopeAndCode(scope, organizationId, workspaceId, clientId, projectId, code)) {
            throw RateCardExceptions.rateCardCodeAlreadyExists(code);
        }

        boolean isDefault = Boolean.TRUE.equals(cmd.isDefault());
        if (isDefault && scope == RateCardScope.WORKSPACE) {
            for (RateCard existing : rateCardRepository.findActiveDefaultsByWorkspaceId(workspaceId)) {
                rateCardRepository.save(existing.setDefault(false));
            }
        }

        RateCard toSave;
        try {
            toSave = RateCard.create(code, cmd.name(), cmd.description(), scope, organizationId, workspaceId,
                    clientId, projectId, currency, isDefault);
        } catch (IllegalArgumentException ex) {
            throw RateCardExceptions.rateCardInvalidScope(scope.name());
        }
        RateCard saved = rateCardRepository.save(toSave);
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD, saved.id(), "RATE_CARD_CREATED",
                RateCardPlatformPublisher.mapOf("id", saved.id(), "code", saved.code(), "scope", saved.scope().name()));
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD, saved.id(),
                RateCardActivityActions.RATE_CARD_CREATED, "Rate card created: " + saved.code());
        return RateCardResponse.from(saved);
    }
}
