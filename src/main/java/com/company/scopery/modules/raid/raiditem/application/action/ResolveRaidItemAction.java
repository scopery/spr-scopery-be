package com.company.scopery.modules.raid.raiditem.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.raid.raiditem.application.command.ResolveRaidItemCommand;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ResolveRaidItemAction {
    private final RaidItemRepository items;
    private final RaidAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final RaidActivityLogger activityLogger;

    public ResolveRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization,
                                 CurrentUserAuthorizationService currentUser, RaidActivityLogger activityLogger) {
        this.items = items;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RaidItemResponse execute(ResolveRaidItemCommand command) {
        authorization.requireUpdate(command.projectId());
        if (command.outcomeNote() == null || command.outcomeNote().isBlank()) {
            throw RaidExceptions.resolutionNoteRequired();
        }
        var actor = currentUser.resolveCurrentUser();
        var item = items.findByIdAndProjectId(command.raidItemId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(command.raidItemId()));
        item = items.save(item.resolve(actor.id(), command.outcomeNote().trim()));
        activityLogger.logSuccess(RaidEntityTypes.ITEM, item.id(), RaidActivityActions.ITEM_RESOLVED, "RAID item resolved");
        return RaidItemResponse.from(item);
    }
}
