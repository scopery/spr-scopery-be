package com.company.scopery.modules.ratecard.inflationpolicy.application.action;

import com.company.scopery.modules.ratecard.inflationpolicy.application.command.CreateInflationPolicyCommand;
import com.company.scopery.modules.ratecard.inflationpolicy.application.response.InflationPolicyResponse;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.CompoundFrequency;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyScope;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicy;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicyRepository;
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

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateInflationPolicyAction {
    private final InflationPolicyRepository repository;
    private final WorkspaceRepository workspaceRepository;
    private final OrganizationRepository organizationRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public CreateInflationPolicyAction(InflationPolicyRepository repository, WorkspaceRepository workspaceRepository,
                                       OrganizationRepository organizationRepository,
                                       RateCardAuthorizationService authorizationService,
                                       RateCardActivityLogger activityLogger, RateCardPlatformPublisher platformPublisher) {
        this.repository = repository; this.workspaceRepository = workspaceRepository;
        this.organizationRepository = organizationRepository; this.authorizationService = authorizationService;
        this.activityLogger = activityLogger; this.platformPublisher = platformPublisher;
    }

    @Transactional
    public InflationPolicyResponse execute(CreateInflationPolicyCommand cmd) {
        InflationPolicyScope scope = RateCardEnumParser.parseRequired(InflationPolicyScope.class, cmd.scope(),
                "INFLATION_POLICY_INVALID_SCOPE", "scope");
        CompoundFrequency frequency = RateCardEnumParser.parseRequired(CompoundFrequency.class, cmd.compoundFrequency(),
                "VALIDATION_ERROR", "compoundFrequency");
        authorizationService.requireInflationCreate(cmd.workspaceId());
        if (cmd.inflationPercent() == null || cmd.inflationPercent().compareTo(BigDecimal.ZERO) < 0) {
            throw RateCardExceptions.inflationInvalidPercent(cmd.inflationPercent());
        }
        if (cmd.effectiveFrom() == null || (cmd.effectiveTo() != null && cmd.effectiveTo().isBefore(cmd.effectiveFrom()))) {
            throw RateCardExceptions.inflationDateRangeInvalid();
        }

        final UUID organizationId;
        final UUID workspaceId;
        if (scope == InflationPolicyScope.SYSTEM) {
            organizationId = null;
            workspaceId = null;
        } else if (scope == InflationPolicyScope.ORGANIZATION) {
            if (cmd.organizationId() == null) throw RateCardExceptions.inflationInvalidScope(scope.name());
            UUID orgId = cmd.organizationId();
            var org = organizationRepository.findById(orgId)
                    .orElseThrow(() -> RateCardExceptions.organizationNotFound(orgId));
            if (org.status() != OrganizationStatus.ACTIVE) throw RateCardExceptions.organizationNotActive(orgId);
            organizationId = orgId;
            workspaceId = null;
        } else {
            if (cmd.workspaceId() == null) throw RateCardExceptions.inflationInvalidScope(scope.name());
            UUID wsId = cmd.workspaceId();
            var ws = workspaceRepository.findById(wsId)
                    .orElseThrow(() -> RateCardExceptions.workspaceNotFound(wsId));
            if (ws.status() != WorkspaceStatus.ACTIVE) throw RateCardExceptions.workspaceNotActive(wsId);
            organizationId = ws.organizationId();
            workspaceId = wsId;
        }

        String code = InflationPolicy.normalizeCode(cmd.code());
        if (repository.existsByScopeAndCode(scope, organizationId, workspaceId, code)) {
            throw RateCardExceptions.inflationCodeAlreadyExists(code);
        }
        InflationPolicy saved = repository.save(InflationPolicy.create(code, cmd.name(), cmd.description(), scope,
                organizationId, workspaceId, cmd.inflationPercent(), frequency, cmd.effectiveFrom(), cmd.effectiveTo()));
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_INFLATION_POLICY, saved.id(),
                "INFLATION_POLICY_CREATED", RateCardPlatformPublisher.mapOf("id", saved.id(), "code", saved.code()));
        activityLogger.logSuccess(RateCardEntityTypes.INFLATION_POLICY, saved.id(),
                RateCardActivityActions.INFLATION_POLICY_CREATED, "Inflation policy created: " + saved.code());
        return InflationPolicyResponse.from(saved);
    }
}
