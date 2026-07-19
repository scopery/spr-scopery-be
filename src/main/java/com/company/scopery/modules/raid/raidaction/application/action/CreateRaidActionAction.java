package com.company.scopery.modules.raid.raidaction.application.action;

import com.company.scopery.modules.raid.raidaction.application.command.CreateRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidAction;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRaidActionAction {
    private final RaidItemRepository items;
    private final RaidActionRepository actions;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public CreateRaidActionAction(RaidItemRepository items, RaidActionRepository actions,
                                  RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.items = items;
        this.actions = actions;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RaidActionResponse execute(CreateRaidActionCommand command) {
        authorization.requireCreate(command.projectId());
        items.findByIdAndProjectId(command.raidItemId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(command.raidItemId()));
        if (command.title() == null || command.title().isBlank()) {
            throw RaidExceptions.titleRequired();
        }
        RaidAction action = actions.save(RaidAction.create(
                command.raidItemId(), command.projectId(), command.title().trim(),
                command.description(), command.ownerUserId(), command.dueDate()));
        activityLogger.logSuccess(RaidEntityTypes.ACTION, action.id(), RaidActivityActions.ACTION_CREATED, "RAID action created");
        return RaidActionResponse.from(action);
    }
}
