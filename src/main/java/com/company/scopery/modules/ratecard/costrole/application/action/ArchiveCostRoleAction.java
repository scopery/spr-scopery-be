package com.company.scopery.modules.ratecard.costrole.application.action;

import com.company.scopery.modules.ratecard.costrole.application.command.ArchiveCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.application.response.CostRoleResponse;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveCostRoleAction {

    private final CostRoleRepository costRoleRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public ArchiveCostRoleAction(CostRoleRepository costRoleRepository,
                                RateCardAuthorizationService authorizationService,
                                RateCardActivityLogger activityLogger,
                                RateCardPlatformPublisher platformPublisher) {
        this.costRoleRepository = costRoleRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public CostRoleResponse execute(ArchiveCostRoleCommand cmd) {
        CostRole role = costRoleRepository.findById(cmd.id())
                .orElseThrow(() -> RateCardExceptions.costRoleNotFound(cmd.id()));

        authorizationService.requireCostRoleArchive(role.workspaceId());
        if (role.status() == CostRoleStatus.ARCHIVED) {
            throw RateCardExceptions.costRoleArchived(cmd.id());
        }
        if (costRoleRepository.isReferencedByRateLines(role.id())) {
            throw RateCardExceptions.costRoleInUse(role.id());
        }
        CostRole saved = costRoleRepository.save(role.archive(authorizationService.currentUserId()));

        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_COST_ROLE, saved.id(),
                "COST_ROLE_ARCHIVED", RateCardPlatformPublisher.mapOf("id", saved.id(), "code", saved.code()));
        activityLogger.logSuccess(RateCardEntityTypes.COST_ROLE, saved.id(),
                RateCardActivityActions.COST_ROLE_ARCHIVED, "Cost role archive: " + saved.code());
        return CostRoleResponse.from(saved);
    }
}
