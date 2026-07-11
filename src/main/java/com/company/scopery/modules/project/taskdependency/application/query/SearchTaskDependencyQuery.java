package com.company.scopery.modules.project.taskdependency.application.query;

import java.util.UUID;

public record SearchTaskDependencyQuery(
        UUID projectId,
        UUID taskId,
        String status,
        int page,
        int size
) {}
