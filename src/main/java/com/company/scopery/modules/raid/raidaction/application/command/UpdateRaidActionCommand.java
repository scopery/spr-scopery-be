package com.company.scopery.modules.raid.raidaction.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateRaidActionCommand(
        UUID projectId,
        UUID raidActionId,
        String title,
        String description,
        UUID ownerUserId,
        LocalDate dueDate
) {}
