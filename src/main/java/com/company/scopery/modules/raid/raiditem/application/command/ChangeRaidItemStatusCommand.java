package com.company.scopery.modules.raid.raiditem.application.command;

import java.util.UUID;

public record ChangeRaidItemStatusCommand(UUID projectId, UUID raidItemId, String status) {}
