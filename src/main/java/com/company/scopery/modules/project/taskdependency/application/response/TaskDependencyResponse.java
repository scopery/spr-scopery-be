package com.company.scopery.modules.project.taskdependency.application.response;

import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;

import java.time.Instant;
import java.util.UUID;

public record TaskDependencyResponse(
        UUID id,
        UUID projectId,
        UUID predecessorTaskId,
        UUID successorTaskId,
        String dependencyType,
        int lagDays,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static TaskDependencyResponse from(TaskDependency d) {
        return new TaskDependencyResponse(
                d.id(),
                d.projectId(),
                d.predecessorTaskId(),
                d.successorTaskId(),
                d.dependencyType().name(),
                d.lagDays(),
                d.status().name(),
                d.createdAt(),
                d.updatedAt()
        );
    }
}
