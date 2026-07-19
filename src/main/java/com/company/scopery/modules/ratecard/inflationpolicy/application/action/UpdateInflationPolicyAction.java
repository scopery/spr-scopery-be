package com.company.scopery.modules.ratecard.inflationpolicy.application.action;

import com.company.scopery.modules.ratecard.inflationpolicy.application.command.UpdateInflationPolicyCommand;
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

import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.CompoundFrequency;
import com.company.scopery.modules.ratecard.shared.util.RateCardEnumParser;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateInflationPolicyAction {
    private final InflationPolicyRepository repository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public UpdateInflationPolicyAction(InflationPolicyRepository repository,
                                       RateCardAuthorizationService authorizationService,
                                       RateCardActivityLogger activityLogger,
                                       RateCardPlatformPublisher platformPublisher) {
        this.repository = repository; this.authorizationService = authorizationService;
        this.activityLogger = activityLogger; this.platformPublisher = platformPublisher;
    }

    @Transactional
    public InflationPolicyResponse execute(UpdateInflationPolicyCommand cmd) {
        InflationPolicy policy = repository.findById(cmd.id())
                .orElseThrow(() -> RateCardExceptions.inflationNotFound(cmd.id()));

        authorizationService.requireInflationUpdate(policy.workspaceId());
        if (policy.status() == InflationPolicyStatus.ARCHIVED) throw RateCardExceptions.inflationArchived(cmd.id());
        if (cmd.inflationPercent() == null || cmd.inflationPercent().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw RateCardExceptions.inflationInvalidPercent(cmd.inflationPercent());
        }
        if (cmd.effectiveFrom() == null || (cmd.effectiveTo() != null && cmd.effectiveTo().isBefore(cmd.effectiveFrom()))) {
            throw RateCardExceptions.inflationDateRangeInvalid();
        }
        CompoundFrequency frequency = RateCardEnumParser.parseRequired(CompoundFrequency.class, cmd.compoundFrequency(),
                "VALIDATION_ERROR", "compoundFrequency");
        InflationPolicy saved = repository.save(policy.update(cmd.name(), cmd.description(), cmd.inflationPercent(),
                frequency, cmd.effectiveFrom(), cmd.effectiveTo()));

        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_INFLATION_POLICY, saved.id(), "INFLATION_POLICY_UPDATED",
                RateCardPlatformPublisher.mapOf("id", saved.id(), "code", saved.code()));
        activityLogger.logSuccess(RateCardEntityTypes.INFLATION_POLICY, saved.id(),
                RateCardActivityActions.INFLATION_POLICY_UPDATED, "Inflation policy update: " + saved.code());
        return InflationPolicyResponse.from(saved);
    }
}
