package com.company.scopery.modules.raid.raidaction.application.command;

import java.util.UUID;

public record CancelRaidActionCommand(UUID projectId, UUID raidActionId) {}
