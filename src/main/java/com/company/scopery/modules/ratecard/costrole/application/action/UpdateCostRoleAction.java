package com.company.scopery.modules.ratecard.costrole.application.action;

import com.company.scopery.modules.ratecard.costrole.application.command.UpdateCostRoleCommand;
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
public class UpdateCostRoleAction {

    private final CostRoleRepository costRoleRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public UpdateCostRoleAction(CostRoleRepository costRoleRepository,
                                RateCardAuthorizationService authorizationService,
                                RateCardActivityLogger activityLogger,
                                RateCardPlatformPublisher platformPublisher) {
        this.costRoleRepository = costRoleRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public CostRoleResponse execute(UpdateCostRoleCommand cmd) {
        CostRole role = costRoleRepository.findById(cmd.id())
                .orElseThrow(() -> RateCardExceptions.costRoleNotFound(cmd.id()));

        authorizationService.requireCostRoleUpdate(role.workspaceId());
        if (role.status() == CostRoleStatus.ARCHIVED) {
            throw RateCardExceptions.costRoleArchived(cmd.id());
        }
        CostRole saved = costRoleRepository.save(role.update(cmd.name(), cmd.description(), cmd.category()));

        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_COST_ROLE, saved.id(),
                "COST_ROLE_UPDATED", RateCardPlatformPublisher.mapOf("id", saved.id(), "code", saved.code()));
        activityLogger.logSuccess(RateCardEntityTypes.COST_ROLE, saved.id(),
                RateCardActivityActions.COST_ROLE_UPDATED, "Cost role update: " + saved.code());
        return CostRoleResponse.from(saved);
    }
}
