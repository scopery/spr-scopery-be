package com.company.scopery.modules.project.templatedependency.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateProjectTemplateTaskDependencyRequest(
        @NotNull UUID predecessorTemplateTaskId,
        @NotNull UUID successorTemplateTaskId,
        String dependencyType,
        Integer lagDays
) {}
