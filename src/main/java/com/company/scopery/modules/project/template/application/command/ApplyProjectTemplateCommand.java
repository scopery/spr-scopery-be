package com.company.scopery.modules.project.template.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record ApplyProjectTemplateCommand(
        UUID templateId,
        UUID versionId,
        UUID workspaceId,
        String projectCode,
        String projectName,
        String projectDescription,
        UUID ownerUserId,
        String defaultCurrency,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        boolean includeTemplateTasks,
        boolean includeTemplateDependencies,
        boolean copyEstimateHours
) {}
