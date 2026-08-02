package com.company.scopery.modules.project.wbs.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateWbsNodeCommand(
        UUID id,
        UUID projectId,
        String title,
        String description,
        String nodeType,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
