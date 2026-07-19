package com.company.scopery.modules.ratecard.ratecardline.application.action;

import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.application.command.CreateRateCardLineCommand;
import com.company.scopery.modules.ratecard.ratecardline.application.response.RateCardLineResponse;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLine;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
import com.company.scopery.modules.ratecard.ratecardversion.domain.enums.RateCardVersionStatus;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersionRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.currency.SupportedCurrency;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CreateRateCardLineAction {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardLineRepository lineRepository;
    private final CostRoleRepository costRoleRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public CreateRateCardLineAction(RateCardRepository rateCardRepository, RateCardVersionRepository versionRepository,
                                    RateCardLineRepository lineRepository, CostRoleRepository costRoleRepository,
                                    RateCardAuthorizationService authorizationService,
                                    RateCardActivityLogger activityLogger, RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository; this.versionRepository = versionRepository;
        this.lineRepository = lineRepository; this.costRoleRepository = costRoleRepository;
        this.authorizationService = authorizationService; this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public RateCardLineResponse execute(CreateRateCardLineCommand cmd) {
        var card = rateCardRepository.findById(cmd.rateCardId())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(cmd.rateCardId()));
        authorizationService.requireRateCardLineCreate(card.workspaceId());
        var version = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> RateCardExceptions.versionNotFound(cmd.versionId()));
        if (!version.rateCardId().equals(card.id())) throw RateCardExceptions.versionNotFound(cmd.versionId());
        if (version.status() != RateCardVersionStatus.DRAFT) throw RateCardExceptions.versionNotDraft(version.id());

        if (cmd.costRatePerHour() == null || cmd.costRatePerHour().compareTo(BigDecimal.ZERO) <= 0) {
            throw RateCardExceptions.invalidCostRate(cmd.costRatePerHour());
        }
        if (cmd.billingRatePerHour() != null && cmd.billingRatePerHour().compareTo(BigDecimal.ZERO) <= 0) {
            throw RateCardExceptions.invalidBillingRate(cmd.billingRatePerHour());
        }
        String currency = SupportedCurrency.requireValid(cmd.currencyCode());
        var role = costRoleRepository.findById(cmd.costRoleId())
                .orElseThrow(() -> RateCardExceptions.costRoleNotFound(cmd.costRoleId()));
        if (role.status() != CostRoleStatus.ACTIVE) throw RateCardExceptions.lineRoleInactive(role.id());
        if (lineRepository.existsDuplicate(version.id(), cmd.costRoleId(), cmd.seniorityLevel(),
                cmd.locationCode(), currency, null)) {
            throw RateCardExceptions.lineDuplicate();
        }

        RateCardLine saved = lineRepository.save(RateCardLine.create(version.id(), cmd.costRoleId(),
                cmd.seniorityLevel(), cmd.locationCode(), currency, cmd.costRatePerHour(),
                cmd.billingRatePerHour(), cmd.notes()));
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD_LINE, saved.id(),
                "RATE_CARD_LINE_CREATED", RateCardPlatformPublisher.mapOf("id", saved.id(), "versionId", version.id()));
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD_LINE, saved.id(),
                RateCardActivityActions.RATE_CARD_LINE_CREATED, "Rate card line created");
        return RateCardLineResponse.from(saved);
    }
}
