package com.company.scopery.modules.project.task.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(UUID id);

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    PageResult<Task> search(UUID projectId, UUID phaseId, UUID wbsNodeId,
                      TaskStatus status, TaskPriority priority,
                      String keyword, PageQuery pageQuery);

    List<Task> findAllByWbsNodeId(UUID wbsNodeId);
}
