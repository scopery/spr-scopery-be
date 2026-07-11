package com.company.scopery.modules.project.project.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectCommand(
        UUID workspaceId,
        String code,
        String name,
        String description,
        UUID ownerUserId,
        String defaultCurrency,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
