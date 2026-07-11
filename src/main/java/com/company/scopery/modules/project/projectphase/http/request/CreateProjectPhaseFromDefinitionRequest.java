package com.company.scopery.modules.project.projectphase.http.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectPhaseFromDefinitionRequest(
        @NotNull UUID phaseDefinitionId,
        int displayOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
