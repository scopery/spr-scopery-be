package com.company.scopery.modules.project.projectphase.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectPhaseFromDefinitionCommand(
        UUID projectId,
        UUID phaseDefinitionId,
        int displayOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
