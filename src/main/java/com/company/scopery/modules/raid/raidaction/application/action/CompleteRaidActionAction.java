package com.company.scopery.modules.raid.raidaction.application.action;

import com.company.scopery.modules.raid.raidaction.application.command.CompleteRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CompleteRaidActionAction {
    private final RaidActionRepository actions;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public CompleteRaidActionAction(RaidActionRepository actions, RaidAuthorizationService authorization,
                                    RaidActivityLogger activityLogger) {
        this.actions = actions;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RaidActionResponse execute(CompleteRaidActionCommand command) {
        authorization.requireUpdate(command.projectId());
        var action = actions.findByIdAndProjectId(command.raidActionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.actionNotFound(command.raidActionId()));
        action = actions.save(action.complete(command.completionNote()));
        activityLogger.logSuccess(RaidEntityTypes.ACTION, action.id(), RaidActivityActions.ACTION_COMPLETED, "RAID action completed");
        return RaidActionResponse.from(action);
    }
}
