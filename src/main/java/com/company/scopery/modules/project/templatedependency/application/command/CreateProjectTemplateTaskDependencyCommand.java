package com.company.scopery.modules.project.templatedependency.application.command;

import java.util.UUID;

public record CreateProjectTemplateTaskDependencyCommand(
        UUID templateId,
        UUID versionId,
        UUID predecessorTemplateTaskId,
        UUID successorTemplateTaskId,
        String dependencyType,
        int lagDays
) {}
