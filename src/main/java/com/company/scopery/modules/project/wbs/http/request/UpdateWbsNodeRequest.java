package com.company.scopery.modules.project.wbs.http.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateWbsNodeRequest(
        @NotBlank String title,
        String description,
        @NotBlank String nodeType,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
