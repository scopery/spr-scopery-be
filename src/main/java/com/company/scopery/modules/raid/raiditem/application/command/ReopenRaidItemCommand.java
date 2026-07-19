package com.company.scopery.modules.raid.raiditem.application.command;

import java.util.UUID;

public record ReopenRaidItemCommand(UUID projectId, UUID raidItemId) {}
