package com.company.scopery.modules.project.templatedependency.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProjectTemplateTaskDependencyJpaRepository
        extends JpaRepository<ProjectTemplateTaskDependencyJpaEntity, UUID> {

    List<ProjectTemplateTaskDependencyJpaEntity> findByTemplateVersionId(UUID templateVersionId);

    boolean existsByPredecessorTemplateTaskIdAndSuccessorTemplateTaskIdAndDependencyType(
            UUID predecessorId, UUID successorId, String dependencyType);

    List<ProjectTemplateTaskDependencyJpaEntity> findByPredecessorTemplateTaskId(UUID predecessorTemplateTaskId);
}
