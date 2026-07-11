package com.company.scopery.modules.project.projectphase.http.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateProjectPhaseRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        int displayOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
