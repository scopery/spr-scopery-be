package com.company.scopery.modules.ratecard.ratecardversion.application.action;

import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardversion.application.command.ArchiveRateCardVersionCommand;
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
public class ArchiveRateCardVersionAction {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public ArchiveRateCardVersionAction(RateCardRepository rateCardRepository, RateCardVersionRepository versionRepository,
                                        RateCardAuthorizationService authorizationService,
                                        RateCardActivityLogger activityLogger, RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository; this.versionRepository = versionRepository;
        this.authorizationService = authorizationService; this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public RateCardVersionResponse execute(ArchiveRateCardVersionCommand cmd) {
        var card = rateCardRepository.findById(cmd.rateCardId())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(cmd.rateCardId()));
        authorizationService.requireRateCardArchive(card.workspaceId());
        RateCardVersion version = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> RateCardExceptions.versionNotFound(cmd.versionId()));
        if (!version.rateCardId().equals(card.id())) throw RateCardExceptions.versionNotFound(cmd.versionId());
        RateCardVersion saved = versionRepository.save(version.archive(authorizationService.currentUserId()));
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD_VERSION, saved.id(),
                "RATE_CARD_VERSION_ARCHIVED", RateCardPlatformPublisher.mapOf("id", saved.id()));
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD_VERSION, saved.id(),
                RateCardActivityActions.RATE_CARD_VERSION_ARCHIVED, "Version archived: " + saved.versionNumber());
        return RateCardVersionResponse.from(saved);
    }
}
