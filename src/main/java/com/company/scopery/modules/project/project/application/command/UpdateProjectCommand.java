package com.company.scopery.modules.project.project.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateProjectCommand(
        UUID id,
        String name,
        String description,
        UUID ownerUserId,
        String defaultCurrency,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
