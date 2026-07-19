package com.company.scopery.modules.ratecard.inflationpolicy.application.action;

import com.company.scopery.modules.ratecard.inflationpolicy.application.command.DeactivateInflationPolicyCommand;
import com.company.scopery.modules.ratecard.inflationpolicy.application.response.InflationPolicyResponse;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyStatus;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicy;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicyRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeactivateInflationPolicyAction {
    private final InflationPolicyRepository repository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public DeactivateInflationPolicyAction(InflationPolicyRepository repository,
                                       RateCardAuthorizationService authorizationService,
                                       RateCardActivityLogger activityLogger,
                                       RateCardPlatformPublisher platformPublisher) {
        this.repository = repository; this.authorizationService = authorizationService;
        this.activityLogger = activityLogger; this.platformPublisher = platformPublisher;
    }

    @Transactional
    public InflationPolicyResponse execute(DeactivateInflationPolicyCommand cmd) {
        InflationPolicy policy = repository.findById(cmd.id())
                .orElseThrow(() -> RateCardExceptions.inflationNotFound(cmd.id()));

        authorizationService.requireInflationUpdate(policy.workspaceId());
        if (policy.status() == InflationPolicyStatus.ARCHIVED) throw RateCardExceptions.inflationArchived(cmd.id());
        InflationPolicy saved = repository.save(policy.deactivate());

        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_INFLATION_POLICY, saved.id(), "INFLATION_POLICY_DEACTIVATED",
                RateCardPlatformPublisher.mapOf("id", saved.id(), "code", saved.code()));
        activityLogger.logSuccess(RateCardEntityTypes.INFLATION_POLICY, saved.id(),
                RateCardActivityActions.INFLATION_POLICY_DEACTIVATED, "Inflation policy deactivate: " + saved.code());
        return InflationPolicyResponse.from(saved);
    }
}
