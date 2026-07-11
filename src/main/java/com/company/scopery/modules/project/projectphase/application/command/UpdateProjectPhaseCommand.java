package com.company.scopery.modules.project.projectphase.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateProjectPhaseCommand(
        UUID id,
        UUID projectId,
        String name,
        String description,
        int displayOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
