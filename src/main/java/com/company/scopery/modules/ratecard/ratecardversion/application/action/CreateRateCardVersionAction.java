package com.company.scopery.modules.ratecard.ratecardversion.application.action;

import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardStatus;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardversion.application.command.CreateRateCardVersionCommand;
import com.company.scopery.modules.ratecard.ratecardversion.application.response.RateCardVersionResponse;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersion;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersionRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRateCardVersionAction {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public CreateRateCardVersionAction(RateCardRepository rateCardRepository,
                                       RateCardVersionRepository versionRepository,
                                       RateCardAuthorizationService authorizationService,
                                       RateCardActivityLogger activityLogger,
                                       RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository;
        this.versionRepository = versionRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public RateCardVersionResponse execute(CreateRateCardVersionCommand cmd) {
        var card = rateCardRepository.findById(cmd.rateCardId())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(cmd.rateCardId()));
        if (card.status() == RateCardStatus.ARCHIVED) throw RateCardExceptions.rateCardArchived(card.id());
        authorizationService.requireRateCardUpdate(card.workspaceId());
        if (cmd.effectiveFrom() == null) throw RateCardExceptions.versionDateRangeInvalid();
        if (cmd.effectiveTo() != null && cmd.effectiveTo().isBefore(cmd.effectiveFrom())) {
            throw RateCardExceptions.versionDateRangeInvalid();
        }
        int next = versionRepository.findMaxVersionNumber(card.id()).orElse(0) + 1;
        RateCardVersion saved = versionRepository.save(
                RateCardVersion.create(card.id(), next, cmd.name(), cmd.description(), cmd.effectiveFrom(), cmd.effectiveTo()));
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD_VERSION, saved.id(),
                "RATE_CARD_VERSION_CREATED", RateCardPlatformPublisher.mapOf("id", saved.id(), "rateCardId", card.id(),
                        "versionNumber", saved.versionNumber()));
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD_VERSION, saved.id(),
                RateCardActivityActions.RATE_CARD_VERSION_CREATED, "Version created: " + saved.versionNumber());
        return RateCardVersionResponse.from(saved);
    }
}
