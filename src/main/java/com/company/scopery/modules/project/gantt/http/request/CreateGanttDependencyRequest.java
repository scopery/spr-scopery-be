package com.company.scopery.modules.project.gantt.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateGanttDependencyRequest(
        @NotNull UUID predecessorTaskId,
        @NotNull UUID successorTaskId,
        String dependencyType,
        Integer lagDays,
        Boolean recalculate
) {}
