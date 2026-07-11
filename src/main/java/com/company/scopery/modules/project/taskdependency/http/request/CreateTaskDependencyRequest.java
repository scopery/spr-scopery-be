package com.company.scopery.modules.project.taskdependency.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTaskDependencyRequest(
        @NotNull UUID predecessorTaskId,
        @NotNull UUID successorTaskId,
        @NotBlank String dependencyType,
        int lagDays
) {}
