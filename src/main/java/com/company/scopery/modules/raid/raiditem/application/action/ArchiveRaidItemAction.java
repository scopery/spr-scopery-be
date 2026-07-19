package com.company.scopery.modules.raid.raiditem.application.action;

import com.company.scopery.modules.raid.raiditem.application.command.ArchiveRaidItemCommand;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemStatus;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveRaidItemAction {
    private final RaidItemRepository items;
    private final RaidAuthorizationService authorization;

    public ArchiveRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization) {
        this.items = items;
        this.authorization = authorization;
    }

    @Transactional
    public RaidItemResponse execute(ArchiveRaidItemCommand command) {
        authorization.requireArchive(command.projectId());
        var item = items.findByIdAndProjectId(command.raidItemId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(command.raidItemId()));
        return RaidItemResponse.from(items.save(item.withStatus(RaidItemStatus.ARCHIVED)));
    }
}
