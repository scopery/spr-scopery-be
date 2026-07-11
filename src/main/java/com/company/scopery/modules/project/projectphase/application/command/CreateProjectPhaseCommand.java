package com.company.scopery.modules.project.projectphase.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectPhaseCommand(
        UUID projectId,
        String code,
        String name,
        String description,
        int displayOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
