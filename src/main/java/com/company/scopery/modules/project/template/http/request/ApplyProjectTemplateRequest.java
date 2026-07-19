package com.company.scopery.modules.project.template.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record ApplyProjectTemplateRequest(
        @NotNull UUID workspaceId,
        @NotBlank @Size(max = 100) String projectCode,
        @NotBlank @Size(max = 255) String projectName,
        String projectDescription,
        UUID ownerUserId,
        String defaultCurrency,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        Boolean includeTemplateTasks,
        Boolean includeTemplateDependencies,
        Boolean copyEstimateHours
) {}
