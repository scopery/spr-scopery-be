package com.company.scopery.modules.raid.raiditem.application.command;

import java.util.UUID;

public record ResolveRaidItemCommand(UUID projectId, UUID raidItemId, String outcomeNote) {}
