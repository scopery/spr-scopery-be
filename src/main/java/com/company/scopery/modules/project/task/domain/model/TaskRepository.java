package com.company.scopery.modules.project.task.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;

import java.time.LocalDate;
import java.util.Collection;
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

    List<Task> findAllByProjectId(UUID projectId);

    /** Phase 20: tasks due on the given date, excluding terminal statuses. */
    List<Task> findDueSoonReminderCandidates(LocalDate dueDate, Collection<TaskStatus> excludedStatuses, int limit);

    /** Phase 20: tasks overdue before the given date, excluding terminal statuses. */
    List<Task> findOverdueReminderCandidates(LocalDate beforeDate, Collection<TaskStatus> excludedStatuses, int limit);
}
