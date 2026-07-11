package com.company.scopery.modules.project.taskdependency.application.command;

import java.util.UUID;

public record CreateTaskDependencyCommand(
        UUID projectId,
        UUID predecessorTaskId,
        UUID successorTaskId,
        String dependencyType,
        int lagDays
) {}
