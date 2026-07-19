package com.company.scopery.modules.ratecard.resolution.application.action;

import com.company.scopery.modules.ratecard.resolution.application.query.ResolveRateQuery;
import com.company.scopery.modules.ratecard.resolution.application.response.RateSnapshotResponse;
import com.company.scopery.modules.ratecard.resolution.application.service.RateResolutionService;
import com.company.scopery.modules.ratecard.resolution.domain.RateSnapshot;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ResolveRateAction {
    private final RateResolutionService rateResolutionService;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardPlatformPublisher platformPublisher;

    public ResolveRateAction(RateResolutionService rateResolutionService,
                             RateCardAuthorizationService authorizationService,
                             RateCardPlatformPublisher platformPublisher) {
        this.rateResolutionService = rateResolutionService;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional(readOnly = true)
    public RateSnapshotResponse execute(ResolveRateQuery query) {
        authorizationService.requireResolutionView(query.workspaceId());
        RateSnapshot snapshot = rateResolutionService.resolve(query);
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_RATE_CARD, snapshot.rateCardId(),
                "RATE_RESOLVED", RateCardPlatformPublisher.mapOf(
                        "rateCardId", snapshot.rateCardId(),
                        "costRoleCode", snapshot.costRoleCode(),
                        "targetDate", snapshot.resolvedForDate().toString(),
                        "adjustedCostRate", snapshot.adjustedCostRate()));
        return RateSnapshotResponse.from(snapshot);
    }
}
