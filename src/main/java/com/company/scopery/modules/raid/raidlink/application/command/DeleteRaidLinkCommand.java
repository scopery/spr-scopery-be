package com.company.scopery.modules.raid.raidlink.application.command;

import java.util.UUID;

public record DeleteRaidLinkCommand(UUID projectId, UUID raidItemId, UUID linkId) {}
