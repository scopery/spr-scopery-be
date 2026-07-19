package com.company.scopery.modules.ratecard.ratecardversion.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLine;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
import com.company.scopery.modules.ratecard.ratecardversion.application.command.PublishRateCardVersionCommand;
import com.company.scopery.modules.ratecard.ratecardversion.application.response.RateCardVersionResponse;
import com.company.scopery.modules.ratecard.ratecardversion.domain.enums.RateCardVersionStatus;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersion;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PublishRateCardVersionAction {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardLineRepository lineRepository;
    private final CostRoleRepository costRoleRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public PublishRateCardVersionAction(RateCardRepository rateCardRepository,
                                        RateCardVersionRepository versionRepository,
                                        RateCardLineRepository lineRepository,
                                        CostRoleRepository costRoleRepository,
                                        RateCardAuthorizationService authorizationService,
                                        RateCardActivityLogger activityLogger,
                                        RateCardPlatformPublisher platformPublisher) {
        this.rateCardRepository = rateCardRepository;
        this.versionRepository = versionRepository;
        this.lineRepository = lineRepository;
        this.costRoleRepository = costRoleRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public RateCardVersionResponse execute(PublishRateCardVersionCommand cmd) {
        var card = rateCardRepository.findById(cmd.rateCardId())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(cmd.rateCardId()));
        authorizationService.requireRateCardPublish(card.workspaceId());
        RateCardVersion version = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> RateCardExceptions.versionNotFound(cmd.versionId()));
        if (!version.rateCardId().equals(card.id())) throw RateCardExceptions.versionNotFound(cmd.versionId());
        if (version.status() == RateCardVersionStatus.PUBLISHED) {
            throw RateCardExceptions.versionAlreadyPublished(version.id());
        }
        if (version.status() != RateCardVersionStatus.DRAFT) {
            throw RateCardExceptions.versionNotDraft(version.id());
        }

        List<RateCardLine> lines = lineRepository.findByVersionId(version.id());
        if (lines.isEmpty()) throw RateCardExceptions.versionNoLines(version.id());

        Set<String> keys = new HashSet<>();
        for (RateCardLine line : lines) {
            if (line.costRatePerHour() == null || line.costRatePerHour().compareTo(BigDecimal.ZERO) <= 0) {
                throw RateCardExceptions.invalidCostRate(line.costRatePerHour());
            }
            if (line.billingRatePerHour() != null && line.billingRatePerHour().compareTo(BigDecimal.ZERO) <= 0) {
                throw RateCardExceptions.invalidBillingRate(line.billingRatePerHour());
            }
            SupportedCurrency.requireValid(line.currencyCode());
            var role = costRoleRepository.findById(line.costRoleId())
                    .orElseThrow(() -> RateCardExceptions.costRoleNotFound(line.costRoleId()));
            if (role.status() != CostRoleStatus.ACTIVE) {
                throw RateCardExceptions.lineRoleInactive(role.id());
            }
            String key = line.costRoleId() + "|" + nullToEmpty(line.seniorityLevel()) + "|"
                    + nullToEmpty(line.locationCode()) + "|" + line.currencyCode();
            if (!keys.add(key)) throw RateCardExceptions.lineDuplicate();
        }

        if (versionRepository.existsPublishedOverlap(card.id(), version.effectiveFrom(), version.effectiveTo(), version.id())) {
            throw RateCardExceptions.versionOverlap(card.id());
        }

        RateCardVersion published = versionRepository.save(version.publish(authorizationService.currentUserId()));
        rateCardRepository.save(card.withCurrentVersion(published.id()));

        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD_VERSION, published.id(),
                "RATE_CARD_VERSION_PUBLISHED", RateCardPlatformPublisher.mapOf(
                        "id", published.id(), "rateCardId", card.id(), "versionNumber", published.versionNumber()));
        platformPublisher.audit(AuditEventType.RATE_CARD_VERSION_PUBLISHED, authorizationService.currentUserId(),
                RateCardPlatformPublisher.AGGREGATE_RATE_CARD_VERSION, published.id(),
                card.organizationId(), card.workspaceId(),
                RateCardPlatformPublisher.mapOf("versionNumber", published.versionNumber()),
                "Rate card version published: " + published.versionNumber());
        activityLogger.logSuccess(RateCardEntityTypes.RATE_CARD_VERSION, published.id(),
                RateCardActivityActions.RATE_CARD_VERSION_PUBLISHED, "Version published: " + published.versionNumber());
        return RateCardVersionResponse.from(published);
    }

    private static String nullToEmpty(String v) { return v == null ? "" : v; }
}
