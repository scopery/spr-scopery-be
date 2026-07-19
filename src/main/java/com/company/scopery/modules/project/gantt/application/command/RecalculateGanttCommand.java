package com.company.scopery.modules.project.gantt.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record RecalculateGanttCommand(
        UUID projectId,
        LocalDate planningStartDate,
        LocalDate planningEndDate,
        boolean includeCompletedTasks,
        boolean markAsCurrent
) {}
