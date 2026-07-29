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

    /**
     * My Work: paginated search for tasks assigned to a specific user across a set of projects.
     *
     * @param projectIds     projects to search within (workspace scope)
     * @param userId         assignee to filter on
     * @param statuses       specific statuses to include; null/empty = no status filter
     * @param excludeTerminal true = exclude DONE/CANCELLED/ARCHIVED regardless of statuses param
     * @param dateFrom       inclusive window start; null = no lower bound
     * @param dateTo         inclusive window end; null = no upper bound
     * @param dueDateOnly    true = only match dueDate (used for OVERDUE); false = use full overlap logic
     * @param includeUndated true = include tasks with no dueDate and no plannedStartDate (ALL_OPEN)
     * @param pageQuery      pagination
     */
    PageResult<Task> searchForUser(Collection<UUID> projectIds, UUID userId,
                                   List<String> statuses, boolean excludeTerminal,
                                   LocalDate dateFrom, LocalDate dateTo,
                                   boolean dueDateOnly, boolean includeUndated,
                                   PageQuery pageQuery);

    /**
     * My Work: count tasks matching the same criteria as {@link #searchForUser} — used for summary metrics.
     */
    long countForUser(Collection<UUID> projectIds, UUID userId,
                      List<String> statuses, boolean excludeTerminal,
                      LocalDate dateFrom, LocalDate dateTo,
                      boolean dueDateOnly, boolean includeUndated);

    /** My Work: count tasks with no dueDate and no plannedStartDate assigned to the user. */
    long countUndatedForUser(Collection<UUID> projectIds, UUID userId, boolean excludeTerminal);

    /**
     * Daily summary: returns DONE tasks completed on {@code date} and all IN_PROGRESS tasks
     * for the given workspace, restricted to the provided assignee user IDs.
     * Each row: [id, code, title, projectId, projectName, projectPhaseId, phaseName,
     *            inChargeUserId, completedAt, startedAt, estimateHours, status]
     */
    List<Object[]> findDailySummaryRows(UUID workspaceId, Collection<UUID> userIds, LocalDate date);
}
