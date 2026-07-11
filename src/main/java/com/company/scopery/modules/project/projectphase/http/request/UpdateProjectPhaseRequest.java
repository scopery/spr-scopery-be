package com.company.scopery.modules.project.projectphase.http.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateProjectPhaseRequest(
        @NotBlank String name,
        String description,
        int displayOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
