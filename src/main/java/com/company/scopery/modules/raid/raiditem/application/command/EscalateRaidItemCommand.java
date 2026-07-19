package com.company.scopery.modules.raid.raiditem.application.command;

import java.util.UUID;

public record EscalateRaidItemCommand(UUID projectId, UUID raidItemId, String escalationLevel, String reason) {}
