package com.company.scopery.modules.raid.raidaction.application.command;

import java.util.UUID;

public record CompleteRaidActionCommand(UUID projectId, UUID raidActionId, String completionNote) {}
