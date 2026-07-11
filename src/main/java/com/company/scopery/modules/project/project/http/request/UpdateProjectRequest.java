package com.company.scopery.modules.project.project.http.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateProjectRequest(
        @NotBlank String name,
        String description,
        UUID ownerUserId,
        String defaultCurrency,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
