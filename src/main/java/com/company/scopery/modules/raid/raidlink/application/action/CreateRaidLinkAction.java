package com.company.scopery.modules.raid.raidlink.application.action;

import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.raidlink.application.command.CreateRaidLinkCommand;
import com.company.scopery.modules.raid.raidlink.application.response.RaidLinkResponse;
import com.company.scopery.modules.raid.raidlink.domain.model.RaidLink;
import com.company.scopery.modules.raid.raidlink.domain.model.RaidLinkRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRaidLinkAction {
    private final RaidItemRepository items;
    private final RaidLinkRepository links;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public CreateRaidLinkAction(RaidItemRepository items, RaidLinkRepository links,
                                RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.items = items;
        this.links = links;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RaidLinkResponse execute(CreateRaidLinkCommand command) {
        authorization.requireUpdate(command.projectId());
        items.findByIdAndProjectId(command.raidItemId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(command.raidItemId()));
        if (command.linkType() == null || command.linkType().isBlank()) {
            throw RaidExceptions.linkTypeRequired();
        }
        if (command.targetType() == null || command.targetType().isBlank()) {
            throw RaidExceptions.targetTypeRequired();
        }
        if (command.targetId() == null) {
            throw RaidExceptions.targetTypeRequired();
        }
        RaidLink link = links.save(RaidLink.create(
                command.raidItemId(), command.projectId(), command.linkType().trim(),
                command.targetType().trim(), command.targetId()));
        activityLogger.logSuccess(RaidEntityTypes.LINK, link.id(), RaidActivityActions.LINK_CREATED, "RAID link created");
        return RaidLinkResponse.from(link);
    }
}
