package com.company.scopery.modules.project.templatedependency.domain.model;

import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectTemplateTaskDependencyRepository {

    ProjectTemplateTaskDependency save(ProjectTemplateTaskDependency dependency);

    Optional<ProjectTemplateTaskDependency> findById(UUID id);

    void deleteById(UUID id);

    List<ProjectTemplateTaskDependency> findByTemplateVersionId(UUID templateVersionId);

    boolean existsByPredecessorAndSuccessorAndType(
            UUID predecessorId, UUID successorId, TaskDependencyType type);

    List<ProjectTemplateTaskDependency> findOutgoingDependencies(UUID predecessorTemplateTaskId);
}
