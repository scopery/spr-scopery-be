package com.company.scopery.modules.ratecard.ratecard.application.action;

import com.company.scopery.modules.ratecard.ratecard.application.command.ArchiveRateCardCommand;
import com.company.scopery.modules.ratecard.ratecard.application.response.RateCardResponse;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardStatus;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCard;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("ratecardArchiveRateCardAction")
public class ArchiveRateCardAction {
    private final RateCardRepository rateCardRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public ArchiveRateCardAction(RateCardRepository rateCardRepository,
                                RateCardAuthorizationService authorizationService,
                                RateCardActivityLogger activityLogger,
                                RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public RateCardResponse execute(ArchiveRateCardCommand cmd) {
        RateCard card = rateCardRepository.findById(cmd.id())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(cmd.id()));

        authorizationService.requireRateCardArchive(card.workspaceId());
        if (card.status() == RateCardStatus.ARCHIVED) throw RateCardExceptions.rateCardArchived(cmd.id());
        RateCard saved = rateCardRepository.save(card.archive(authorizationService.currentUserId()));

        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD, saved.id(), "RATE_CARD_ARCHIVED",
                RateCardPlatformPublisher.mapOf("id", saved.id(), "code", saved.code()));
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD, saved.id(),
                RateCardActivityActions.RATE_CARD_ARCHIVED, "Rate card archive: " + saved.code());
        return RateCardResponse.from(saved);
    }
}
