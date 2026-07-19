package com.company.scopery.modules.raid.raiditem.application.command;

import java.util.UUID;

public record ArchiveRaidItemCommand(UUID projectId, UUID raidItemId) {}
