package com.company.scopery.modules.raid.raiditem.application.action;

import com.company.scopery.modules.raid.raiditem.application.command.ReopenRaidItemCommand;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReopenRaidItemAction {
    private final RaidItemRepository items;
    private final RaidAuthorizationService authorization;

    public ReopenRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization) {
        this.items = items;
        this.authorization = authorization;
    }

    @Transactional
    public RaidItemResponse execute(ReopenRaidItemCommand command) {
        authorization.requireUpdate(command.projectId());
        var item = items.findByIdAndProjectId(command.raidItemId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(command.raidItemId()));
        return RaidItemResponse.from(items.save(item.reopen()));
    }
}
