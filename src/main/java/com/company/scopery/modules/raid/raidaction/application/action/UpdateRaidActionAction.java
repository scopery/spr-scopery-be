package com.company.scopery.modules.raid.raidaction.application.action;

import com.company.scopery.modules.raid.raidaction.application.command.UpdateRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.domain.enums.RaidActionStatus;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRaidActionAction {
    private final RaidActionRepository actions;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public UpdateRaidActionAction(RaidActionRepository actions, RaidAuthorizationService authorization,
                                  RaidActivityLogger activityLogger) {
        this.actions = actions;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RaidActionResponse execute(UpdateRaidActionCommand command) {
        authorization.requireUpdate(command.projectId());
        var action = actions.findByIdAndProjectId(command.raidActionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.actionNotFound(command.raidActionId()));
        if (action.status() != RaidActionStatus.OPEN) {
            throw RaidExceptions.actionNotOpen();
        }
        if (command.title() == null || command.title().isBlank()) {
            throw RaidExceptions.titleRequired();
        }
        action = actions.save(action.update(
                command.title().trim(), command.description(), command.ownerUserId(), command.dueDate()));
        activityLogger.logSuccess(RaidEntityTypes.ACTION, action.id(), RaidActivityActions.ACTION_UPDATED, "RAID action updated");
        return RaidActionResponse.from(action);
    }
}
