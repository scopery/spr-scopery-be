package com.company.scopery.modules.ratecard.ratecardversion.application.action;

import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLine;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
import com.company.scopery.modules.ratecard.ratecardversion.application.command.DuplicateRateCardVersionCommand;
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
public class DuplicateRateCardVersionAction {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardLineRepository lineRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public DuplicateRateCardVersionAction(RateCardRepository rateCardRepository,
                                          RateCardVersionRepository versionRepository,
                                          RateCardLineRepository lineRepository,
                                          RateCardAuthorizationService authorizationService,
                                          RateCardActivityLogger activityLogger,
                                          RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository; this.versionRepository = versionRepository;
        this.lineRepository = lineRepository; this.authorizationService = authorizationService;
        this.activityLogger = activityLogger; this.platformPublisher = platformPublisher;
    }

    @Transactional
    public RateCardVersionResponse execute(DuplicateRateCardVersionCommand cmd) {
        var card = rateCardRepository.findById(cmd.rateCardId())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(cmd.rateCardId()));
        authorizationService.requireRateCardCreate(card.workspaceId());
        RateCardVersion source = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> RateCardExceptions.versionNotFound(cmd.versionId()));
        if (!source.rateCardId().equals(card.id())) throw RateCardExceptions.versionNotFound(cmd.versionId());

        int next = versionRepository.findMaxVersionNumber(card.id()).orElse(0) + 1;
        RateCardVersion draft = versionRepository.save(RateCardVersion.create(
                card.id(), next, source.name(), source.description(), source.effectiveFrom(), source.effectiveTo()));
        for (RateCardLine line : lineRepository.findByVersionId(source.id())) {
            lineRepository.save(RateCardLine.create(draft.id(), line.costRoleId(), line.seniorityLevel(),
                    line.locationCode(), line.currencyCode(), line.costRatePerHour(),
                    line.billingRatePerHour(), line.notes()));
        }
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD_VERSION, draft.id(),
                "RATE_CARD_VERSION_DUPLICATED", RateCardPlatformPublisher.mapOf(
                        "id", draft.id(), "sourceVersionId", source.id(), "versionNumber", draft.versionNumber()));
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD_VERSION, draft.id(),
                RateCardActivityActions.RATE_CARD_VERSION_DUPLICATED, "Version duplicated: " + draft.versionNumber());
        return RateCardVersionResponse.from(draft);
    }
}
