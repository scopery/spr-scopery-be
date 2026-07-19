package com.company.scopery.modules.raid.raidlink.application.action;

import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.raidlink.application.command.DeleteRaidLinkCommand;
import com.company.scopery.modules.raid.raidlink.domain.model.RaidLinkRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteRaidLinkAction {
    private final RaidItemRepository items;
    private final RaidLinkRepository links;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public DeleteRaidLinkAction(RaidItemRepository items, RaidLinkRepository links,
                                RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.items = items;
        this.links = links;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteRaidLinkCommand command) {
        authorization.requireUpdate(command.projectId());
        items.findByIdAndProjectId(command.raidItemId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(command.raidItemId()));
        var link = links.findByIdAndProjectId(command.linkId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.linkNotFound(command.linkId()));
        if (!link.raidItemId().equals(command.raidItemId())) {
            throw RaidExceptions.linkNotFound(command.linkId());
        }
        links.deleteByIdAndProjectId(command.linkId(), command.projectId());
        activityLogger.logSuccess(RaidEntityTypes.LINK, command.linkId(), RaidActivityActions.LINK_DELETED, "RAID link deleted");
    }
}
