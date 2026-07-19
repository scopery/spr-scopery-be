package com.company.scopery.modules.raid.raidlink.application.command;

import java.util.UUID;

public record CreateRaidLinkCommand(
        UUID projectId,
        UUID raidItemId,
        String linkType,
        String targetType,
        UUID targetId
) {}
