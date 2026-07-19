package com.company.scopery.modules.raid.raidaction.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateRaidActionCommand(
        UUID projectId,
        UUID raidItemId,
        String title,
        String description,
        UUID ownerUserId,
        LocalDate dueDate
) {}
