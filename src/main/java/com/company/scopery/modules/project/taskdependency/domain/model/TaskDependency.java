package com.company.scopery.modules.project.taskdependency.domain.model;

import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyStatus;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;

import java.time.Instant;
import java.util.UUID;

public record TaskDependency(
        UUID id,
        UUID projectId,
        UUID predecessorTaskId,
        UUID successorTaskId,
        TaskDependencyType dependencyType,
        int lagDays,
        TaskDependencyStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static TaskDependency create(
            UUID projectId,
            UUID predecessorTaskId,
            UUID successorTaskId,
            TaskDependencyType dependencyType,
            int lagDays) {
        return new TaskDependency(
                UUID.randomUUID(),
                projectId,
                predecessorTaskId,
                successorTaskId,
                dependencyType,
                lagDays,
                TaskDependencyStatus.ACTIVE,
                null,
                null
        );
    }

    public TaskDependency deactivate() {
        return new TaskDependency(
                this.id,
                this.projectId,
                this.predecessorTaskId,
                this.successorTaskId,
                this.dependencyType,
                this.lagDays,
                TaskDependencyStatus.INACTIVE,
                this.createdAt,
                this.updatedAt
        );
    }
}
