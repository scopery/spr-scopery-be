package com.company.scopery.modules.raid.raidaction.application.response;

import com.company.scopery.modules.raid.raidaction.domain.model.RaidAction;

import java.util.UUID;

public record CreateLinkedTaskFromRaidActionResponse(RaidActionResponse action, UUID taskId) {
    public static CreateLinkedTaskFromRaidActionResponse from(RaidAction action) {
        return new CreateLinkedTaskFromRaidActionResponse(RaidActionResponse.from(action), action.linkedTaskId());
    }
}
