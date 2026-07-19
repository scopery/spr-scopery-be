package com.company.scopery.modules.project.gantt.http.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RecalculateGanttRequest(
        @NotNull LocalDate planningStartDate,
        @NotNull LocalDate planningEndDate,
        Boolean includeCompletedTasks,
        Boolean markAsCurrent
) {}
