package com.company.scopery.modules.raid.raiditem.application.action;

import com.company.scopery.modules.raid.raiditem.application.command.UpdateRaidItemCommand;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.*;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import com.company.scopery.modules.raid.shared.util.RaidEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRaidItemAction {
    private final RaidItemRepository items;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public UpdateRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization,
                                RaidActivityLogger activityLogger) {
        this.items = items;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RaidItemResponse execute(UpdateRaidItemCommand command) {
        authorization.requireUpdate(command.projectId());
        var item = items.findByIdAndProjectId(command.raidItemId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(command.raidItemId()));
        String title = command.title() != null ? command.title() : item.title();
        if (title == null || title.isBlank()) {
            throw RaidExceptions.titleRequired();
        }
        item = item.update(
                title.trim(),
                command.description() != null ? command.description() : item.description(),
                command.ownerUserId() != null ? command.ownerUserId() : item.ownerUserId(),
                command.severity() != null ? command.severity() : item.severity(),
                command.issueCategory() != null ? command.issueCategory() : item.issueCategory(),
                command.impactSummary() != null ? command.impactSummary() : item.impactSummary(),
                command.rootCause() != null ? command.rootCause() : item.rootCause(),
                command.resolutionPlan() != null ? command.resolutionPlan() : item.resolutionPlan(),
                command.dependencyType() != null ? command.dependencyType() : item.dependencyType(),
                command.assumptionStatement() != null ? command.assumptionStatement() : item.assumptionStatement(),
                command.validationStatus() != null ? command.validationStatus() : item.validationStatus());
        if (item.type() == RaidItemType.RISK
                && (command.probability() != null || command.impact() != null || command.riskResponseStrategy() != null)) {
            RaidProbability p = command.probability() == null ? item.probability()
                    : RaidEnumParser.parseRequired(RaidProbability.class, command.probability(), "probability");
            RaidImpact i = command.impact() == null ? item.impact()
                    : RaidEnumParser.parseRequired(RaidImpact.class, command.impact(), "impact");
            RiskResponseStrategy s = command.riskResponseStrategy() == null ? item.riskResponseStrategy()
                    : RaidEnumParser.parseRequired(RiskResponseStrategy.class, command.riskResponseStrategy(), "riskResponseStrategy");
            item = item.withRiskFields(p, i, s,
                    command.riskTrigger() != null ? command.riskTrigger() : item.riskTrigger());
        }
        item = items.save(item);
        activityLogger.logSuccess(RaidEntityTypes.ITEM, item.id(), RaidActivityActions.ITEM_UPDATED, "RAID item updated");
        return RaidItemResponse.from(item);
    }
}
