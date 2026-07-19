package com.company.scopery.modules.project.taskdependency.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskDependencyJpaRepository
        extends JpaRepository<TaskDependencyJpaEntity, UUID>,
                JpaSpecificationExecutor<TaskDependencyJpaEntity> {

    boolean existsByPredecessorTaskIdAndSuccessorTaskIdAndDependencyType(
            UUID predecessorTaskId, UUID successorTaskId, String dependencyType);

    @Query("SELECT d FROM TaskDependencyJpaEntity d WHERE d.successorTaskId = :taskId AND d.status = 'ACTIVE'")
    List<TaskDependencyJpaEntity> findActiveDependenciesFrom(@Param("taskId") UUID taskId);

    @Query("SELECT d FROM TaskDependencyJpaEntity d WHERE d.predecessorTaskId = :taskId AND d.status = 'ACTIVE'")
    List<TaskDependencyJpaEntity> findActiveDependenciesOutgoing(@Param("taskId") UUID taskId);

    List<TaskDependencyJpaEntity> findAllByProjectIdAndStatus(UUID projectId, String status);
}
