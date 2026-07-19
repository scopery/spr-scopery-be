package com.company.scopery.modules.raid.raiditem.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.raid.raiditem.application.command.EscalateRaidItemCommand;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidEscalationLevel;
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
public class EscalateRaidItemAction {
    private final RaidItemRepository items;
    private final RaidAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final RaidActivityLogger activityLogger;

    public EscalateRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser, RaidActivityLogger activityLogger) {
        this.items = items;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RaidItemResponse execute(EscalateRaidItemCommand command) {
        authorization.requireEscalate(command.projectId());
        if (command.reason() == null || command.reason().isBlank()) {
            throw RaidExceptions.escalationReasonRequired();
        }
        var actor = currentUser.resolveCurrentUser();
        var item = items.findByIdAndProjectId(command.raidItemId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(command.raidItemId()));
        RaidEscalationLevel escalationLevel = RaidEnumParser.parseRequired(
                RaidEscalationLevel.class, command.escalationLevel(), "escalationLevel");
        item = items.save(item.escalate(actor.id(), escalationLevel, command.reason().trim()));
        activityLogger.logSuccess(RaidEntityTypes.ITEM, item.id(), RaidActivityActions.ITEM_ESCALATED, "RAID item escalated");
        return RaidItemResponse.from(item);
    }
}
