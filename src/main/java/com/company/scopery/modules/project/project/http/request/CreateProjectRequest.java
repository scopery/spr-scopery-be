package com.company.scopery.modules.project.project.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectRequest(
        @NotNull UUID workspaceId,
        @NotBlank String code,
        @NotBlank String name,
        String description,
        UUID ownerUserId,
        String defaultCurrency,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {}
