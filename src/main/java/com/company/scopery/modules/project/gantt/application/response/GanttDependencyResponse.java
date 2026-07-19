package com.company.scopery.modules.project.gantt.application.response;

import java.util.UUID;

public record GanttDependencyResponse(
        UUID id,
        UUID predecessorTaskId,
        UUID successorTaskId,
        String predecessorItemId,
        String successorItemId,
        String dependencyType,
        int lagDays,
        boolean unsupportedType
) {}
