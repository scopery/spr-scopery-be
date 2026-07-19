package com.company.scopery.modules.ratecard.ratecardline.application.action;

import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.application.command.DeleteRateCardLineCommand;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
import com.company.scopery.modules.ratecard.ratecardversion.domain.enums.RateCardVersionStatus;
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
public class DeleteRateCardLineAction {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardLineRepository lineRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public DeleteRateCardLineAction(RateCardRepository rateCardRepository, RateCardVersionRepository versionRepository,
                                    RateCardLineRepository lineRepository,
                                    RateCardAuthorizationService authorizationService,
                                    RateCardActivityLogger activityLogger, RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository; this.versionRepository = versionRepository;
        this.lineRepository = lineRepository; this.authorizationService = authorizationService;
        this.activityLogger = activityLogger; this.platformPublisher = platformPublisher;
    }

    @Transactional
    public void execute(DeleteRateCardLineCommand cmd) {
        var card = rateCardRepository.findById(cmd.rateCardId())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(cmd.rateCardId()));
        authorizationService.requireRateCardLineDelete(card.workspaceId());
        var version = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> RateCardExceptions.versionNotFound(cmd.versionId()));
        if (!version.rateCardId().equals(card.id())) throw RateCardExceptions.versionNotFound(cmd.versionId());
        if (version.status() != RateCardVersionStatus.DRAFT) throw RateCardExceptions.versionNotDraft(version.id());
        var line = lineRepository.findById(cmd.lineId())
                .orElseThrow(() -> RateCardExceptions.lineNotFound(cmd.lineId()));
        if (!line.rateCardVersionId().equals(version.id())) throw RateCardExceptions.lineNotFound(cmd.lineId());
        lineRepository.delete(line.id());
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD_LINE, line.id(),
                "RATE_CARD_LINE_DELETED", RateCardPlatformPublisher.mapOf("id", line.id()));
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD_LINE, line.id(),
                RateCardActivityActions.RATE_CARD_LINE_DELETED, "Rate card line deleted");
    }
}
