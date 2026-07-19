package com.company.scopery.modules.project.gantt.application.command;

import java.util.UUID;

public record CreateGanttDependencyCommand(
        UUID projectId,
        UUID predecessorTaskId,
        UUID successorTaskId,
        String dependencyType,
        int lagDays,
        boolean recalculate
) {}
