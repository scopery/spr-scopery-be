package com.company.scopery.modules.project.templatedependency.domain.model;

import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateTaskDependency(
        UUID id,
        UUID templateVersionId,
        UUID predecessorTemplateTaskId,
        UUID successorTemplateTaskId,
        TaskDependencyType dependencyType,
        int lagDays,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectTemplateTaskDependency create(
            UUID templateVersionId,
            UUID predecessorTemplateTaskId,
            UUID successorTemplateTaskId,
            TaskDependencyType dependencyType,
            int lagDays) {
        return new ProjectTemplateTaskDependency(
                UUID.randomUUID(),
                templateVersionId,
                predecessorTemplateTaskId,
                successorTemplateTaskId,
                dependencyType,
                lagDays,
                null,
                null
        );
    }
}
