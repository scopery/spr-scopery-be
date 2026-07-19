package com.company.scopery.modules.project.gantt.application.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * CPM result over task dependency graph.
 *
 * <p>Assumptions:
 * <ul>
 *   <li>Uses FINISH_TO_START dependencies only; other types are ignored for CPM edges.</li>
 *   <li>Task durations derive from scheduled/planned start-finish dates in the Gantt projection
 *       (minimum 1 calendar day when both dates exist).</li>
 *   <li>Tasks without resolvable dates are excluded from CPM.</li>
 *   <li>Slack is measured in whole calendar days ({@code latestStart - earliestStart}).</li>
 * </ul>
 */
public record GanttCriticalPathResponse(
        UUID projectId,
        UUID scheduleRunId,
        List<GanttCriticalPathTaskResponse> tasks,
        List<UUID> criticalTaskIds
) {}
