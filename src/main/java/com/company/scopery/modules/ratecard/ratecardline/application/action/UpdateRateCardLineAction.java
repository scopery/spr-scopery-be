package com.company.scopery.modules.ratecard.ratecardline.application.action;

import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.application.command.UpdateRateCardLineCommand;
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
public class UpdateRateCardLineAction {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardLineRepository lineRepository;
    private final CostRoleRepository costRoleRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public UpdateRateCardLineAction(RateCardRepository rateCardRepository, RateCardVersionRepository versionRepository,
                                    RateCardLineRepository lineRepository, CostRoleRepository costRoleRepository,
                                    RateCardAuthorizationService authorizationService,
                                    RateCardActivityLogger activityLogger, RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository; this.versionRepository = versionRepository;
        this.lineRepository = lineRepository; this.costRoleRepository = costRoleRepository;
        this.authorizationService = authorizationService; this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public RateCardLineResponse execute(UpdateRateCardLineCommand cmd) {
        var card = rateCardRepository.findById(cmd.rateCardId())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(cmd.rateCardId()));
        authorizationService.requireRateCardLineUpdate(card.workspaceId());
        var version = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> RateCardExceptions.versionNotFound(cmd.versionId()));
        if (!version.rateCardId().equals(card.id())) throw RateCardExceptions.versionNotFound(cmd.versionId());
        if (version.status() != RateCardVersionStatus.DRAFT) throw RateCardExceptions.versionNotDraft(version.id());
        RateCardLine line = lineRepository.findById(cmd.lineId())
                .orElseThrow(() -> RateCardExceptions.lineNotFound(cmd.lineId()));
        if (!line.rateCardVersionId().equals(version.id())) throw RateCardExceptions.lineNotFound(cmd.lineId());

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
                cmd.locationCode(), currency, line.id())) {
            throw RateCardExceptions.lineDuplicate();
        }

        RateCardLine saved = lineRepository.save(line.update(cmd.costRoleId(), cmd.seniorityLevel(), cmd.locationCode(),
                currency, cmd.costRatePerHour(), cmd.billingRatePerHour(), cmd.notes()));
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD_LINE, saved.id(),
                "RATE_CARD_LINE_UPDATED", RateCardPlatformPublisher.mapOf("id", saved.id()));
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD_LINE, saved.id(),
                RateCardActivityActions.RATE_CARD_LINE_UPDATED, "Rate card line updated");
        return RateCardLineResponse.from(saved);
    }
}
