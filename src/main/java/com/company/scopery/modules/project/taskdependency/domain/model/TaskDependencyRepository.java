package com.company.scopery.modules.project.taskdependency.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyStatus;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskDependencyRepository {

    TaskDependency save(TaskDependency dep);

    Optional<TaskDependency> findById(UUID id);

    void deleteById(UUID id);

    boolean existsByPredecessorAndSuccessorAndType(UUID predecessorId, UUID successorId, TaskDependencyType type);

    List<TaskDependency> findActiveDependenciesFrom(UUID taskId);

    PageResult<TaskDependency> search(UUID projectId, UUID taskId, TaskDependencyStatus status, PageQuery pageQuery);
}
