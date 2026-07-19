package com.company.scopery.modules.project.templatedependency.application.response;

import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateTaskDependencyResponse(
        UUID id,
        UUID templateVersionId,
        UUID predecessorTemplateTaskId,
        UUID successorTemplateTaskId,
        String dependencyType,
        int lagDays,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectTemplateTaskDependencyResponse from(ProjectTemplateTaskDependency d) {
        return new ProjectTemplateTaskDependencyResponse(
                d.id(), d.templateVersionId(),
                d.predecessorTemplateTaskId(), d.successorTemplateTaskId(),
                d.dependencyType() != null ? d.dependencyType().name() : null,
                d.lagDays(), d.createdAt(), d.updatedAt()
        );
    }
}
